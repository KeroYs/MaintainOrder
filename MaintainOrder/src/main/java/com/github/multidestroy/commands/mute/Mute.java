package com.github.multidestroy.commands.mute;

import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.CommandCreator;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.commands.assets.MuteCreator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.time.Instant;


public class Mute extends Command {
    private final MuteSystem muteSystem;
    private final Messages messages;
    private final Config config;
    private final Config notificationsConfig;
    private final CommandCreator creator;

    public Mute(MuteSystem muteSystem, Messages messages, Config config, Config notificationsConfig) {
        super("mute", CommandPermissions.mute);
        this.muteSystem = muteSystem;
        this.messages = messages;
        this.config = config;
        this.notificationsConfig = notificationsConfig;
        this.creator = new MuteCreator(notificationsConfig);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String reason;
        ServerInfo server;
        TextComponent correctUsage;

        if(sender instanceof ProxiedPlayer) {
            //Player
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.mute.hover_event", "commands.mute.game_correct_usage");
            if(args.length < 3)
                sender.sendMessage( correctUsage );
            else {
                server = ((ProxiedPlayer) sender).getServer().getInfo();
                reason = Utils.mergeArray(args, 2);

                start(sender, correctUsage, args[1], reason, args[0], server);
            }
        } else {
            //Console
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.mute.hover_event", "commands.mute.console_correct_usage");
            if(args.length < 4)
                sender.sendMessage( correctUsage );
            else {
                server = ProxyServer.getInstance().getServerInfo(args[0]);
                if(server == null)
                    sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.wrong_server_name")));
                else {
                    reason = Utils.mergeArray(args, 3);

                    start(sender, correctUsage, args[2], reason, args[1], server);
                }
            }
        }
    }

    private void start(CommandSender giver, TextComponent correctUsage, String time, String reason, String receiver, ServerInfo server) {
        if(!Utils.isUnderLimit(giver, notificationsConfig, receiver, reason))
            return;

        if(creator.timeCorrectness(giver, correctUsage, time)) {
            ProxiedPlayer playerToMute = ProxyServer.getInstance().getPlayer(receiver);
            //if the player is online and plays on the same server
            if(playerToMute != null && server.getName().equals(playerToMute.getServer().getInfo().getName())) {

                //get current expiration and count new expiration
                Instant now = Instant.now();
                Instant currExpiration = muteSystem.getPlayerMuteExpiration(server, playerToMute.getName());
                Instant newExpiration = creator.argTimeConvert(time, now);

                if (canMute(currExpiration, newExpiration)) {
                    //mute player
                    muteSystem.givePlayerMute(server, playerToMute.getName(), newExpiration);
                    Utils.sendGlobalMessage(server, messages.getMuteGlobal(playerToMute.getName(), giver.getName(), reason, creator.translateArgTime(time)));
                    SoundChannel.sendServerSound(server, config.get().getString("sound.mute"));
                    if(!(giver instanceof ProxiedPlayer))
                        giver.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.mute.muted")));
                } else
                    giver.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.mute.already_muted")));
            } else
                giver.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.player_offline")));
        }
    }

    private boolean canMute(Instant currExpiration, Instant newExpiration) {
        if (currExpiration == null)
            return true;
        else return newExpiration.isAfter(currExpiration);
    }
}