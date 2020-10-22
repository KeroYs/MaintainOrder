package com.github.multidestroy.commands.mute;

import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.CommandCreator;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.commands.assets.MuteCreator;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.time.Instant;


public class Mute extends Command {
    private final MuteSystem muteSystem;
    private final Messages messages;
    private final Config config;
    private final CommandCreator creator;

    public Mute(MuteSystem muteSystem, Messages messages, Config config) {
        super("mute", CommandPermissions.mute);
        this.muteSystem = muteSystem;
        this.messages = messages;
        this.config = config;
        this.creator = new MuteCreator(messages);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String reason;
        ServerInfo server;
        TextComponent correctUsage;

        if(sender instanceof ProxiedPlayer) {
            //Player
            correctUsage =
                    Utils.createHoverEvent(
                        messages.getString("NORMAL.COMMAND.MUTE.GAME_CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.MUTE.HOVER_EVENT"
                        ));


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
                    new TextComponent(messages.getString("NORMAL.COMMAND.MUTE.CONSOLE_CORRECT_USAGE"));
            if(args.length < 4)
                sender.sendMessage( correctUsage );
            else {
                server = ProxyServer.getInstance().getServerInfo(args[0]);
                if(server == null)
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.WRONG_SERVER_NAME")));
                else {
                    reason = Utils.mergeArray(args, 3);

                    start(sender, correctUsage, args[2], reason, args[1], server);
                }
            }
        }
    }

    private void start(CommandSender giver, TextComponent correctUsage, String time, String reason, String receiver, ServerInfo server) {
        if(!Utils.isUnderLimit(giver, messages, receiver, reason))
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

                    SpecialTypeInfo specialTypeInfo = new SpecialTypeInfo();
                    {
                        specialTypeInfo.setReceiver(playerToMute.getName());
                        specialTypeInfo.setGiver(giver.getName());
                        specialTypeInfo.setReason(reason);
                        specialTypeInfo.setTime(creator.translateArgTime(time));
                    }

                    Utils.sendGlobalMessage(server, TextComponent.fromLegacyText(messages.getSpecialMessage(
                            SpecialType.COMMAND_MUTE_GLOBAL,
                            specialTypeInfo
                    )));
                    SoundChannel.sendServerSound(server, config.get().getString("sound.mute"));
                    if(!(giver instanceof ProxiedPlayer))
                        giver.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.MUTE.SUCCESS")));
                } else
                    giver.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.MUTE.ALREADY_MUTED")));
            } else
                giver.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.PLAYER_OFFLINE")));
        }
    }

    private boolean canMute(Instant currExpiration, Instant newExpiration) {
        if (currExpiration == null)
            return true;
        else return newExpiration.isAfter(currExpiration);
    }
}