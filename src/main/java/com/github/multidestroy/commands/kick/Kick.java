package com.github.multidestroy.commands.kick;

import com.github.multidestroy.Config;
import com.github.multidestroy.Messages;
import com.github.multidestroy.SoundChannel;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.management.Notification;

public class Kick extends Command {
    private final Messages messages;
    private final Config config;
    private final Config notificationsConfig;

    public Kick(Messages messages, Config config, Config notificationsConfig) {
        super("kick", CommandPermissions.kick);
        this.messages = messages;
        this.config = config;
        this.notificationsConfig = notificationsConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String playerToKick;
        String reason;
        ServerInfo server;
        TextComponent correctUsage;
        if (sender instanceof ProxiedPlayer) {
            //Player
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.kick.hover_event", "commands.kick.game_correct_usage");

            if (args.length < 2) {
                sender.sendMessage( correctUsage );
                return;
            }
            playerToKick = args[0];
            reason = Utils.mergeArray(args, 1);
            server = ((ProxiedPlayer) sender).getServer().getInfo();
        }
        else {
            //Console
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.kick.hover_event", "commands.kick.console_correct_usage");

            if (args.length < 3) {
                sender.sendMessage( correctUsage );
                return;
            }
            playerToKick = args[1];
            reason = Utils.mergeArray(args, 2);
            server = ProxyServer.getInstance().getServers().get(args[0]);
            if (server == null) {
                sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.wrong_server_name")));
                return;
            }
        }

        if(!Utils.isUnderLimit(sender, notificationsConfig, playerToKick, reason))
            return;

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerToKick);
        if(player != null && player.getServer().getInfo().getName().equals(server.getName())) {
            //kick player
            player.disconnect(messages.getKickReceiver(sender.getName(), reason));
            Utils.sendGlobalMessage(server, messages.getKickGlobal(player.getName(), sender.getName(), reason));
            SoundChannel.sendServerSound(server, config.get().getString("sound.kick"));
        } else
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.player_offline")));
    }

}
