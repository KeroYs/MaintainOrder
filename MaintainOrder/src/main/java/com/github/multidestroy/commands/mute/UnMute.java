package com.github.multidestroy.commands.mute;

import com.github.multidestroy.Config;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.MuteSystem;
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

public class UnMute extends Command {

    private final MuteSystem muteSystem;
    private Config notificationsConfig;

    public UnMute(MuteSystem muteSystem, Config notificationsConfig) {
        super("unmute", CommandPermissions.unmute);
        this.muteSystem = muteSystem;
        this.notificationsConfig = notificationsConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage;
        if (sender instanceof ProxiedPlayer) {
            //Player
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.unmute.hover_event", "commands.unmute.game_correct_usage");
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerInfo server = player.getServer().getInfo();
            if (correctnessByGame(args, sender, correctUsage))
                unMutePlayer(server, sender, args[0]);
        } else {
            //Console
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.unmute.hover_event", "commands.unmute.console_correct_usage");
            if (args.length != 0) {
                ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0]);
                if (correctnessByConsole(server, sender, args, correctUsage))
                    unMutePlayer(server, sender, args[1]);
            } else
                sender.sendMessage( correctUsage );
        }
    }

    private void unMutePlayer(ServerInfo server, CommandSender sender, String playerName) {
        if(!Utils.isUnderLimit(sender, notificationsConfig, playerName))
            return;
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        Instant expiration;
        if(player != null)
            expiration = muteSystem.getPlayerMuteExpiration(server, player.getName());
        else
            expiration = muteSystem.getPlayerMuteExpiration(server, playerName);

        if (expiration == null)
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.unmute.not_muted")));
        else {
            muteSystem.removePlayerMute(server, playerName);
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.unmute.unmuted")));
        }
    }

    private boolean correctnessByGame(String[] args, CommandSender sender, TextComponent correctUsage) {
        if (args.length != 1) {
            sender.sendMessage( correctUsage );
            return false;
        }
        return true;
    }

    private boolean correctnessByConsole(ServerInfo server, CommandSender sender, String[] args, TextComponent correctUsage) {
        if (args.length == 2) {
            if (server == null) {
                sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.wrong_server_name")));
                return false;
            }
            return true;
        } else sender.sendMessage( correctUsage );
        return false;
    }
}