package com.github.multidestroy.commands.mute;

import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.MuteSystem;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.time.Instant;

public class UnMute extends Command {

    private final MuteSystem muteSystem;
    private Messages messages;

    public UnMute(MuteSystem muteSystem, Messages messages) {
        super("unmute", CommandPermissions.unmute);
        this.muteSystem = muteSystem;
        this.messages = messages;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage;
        if (sender instanceof ProxiedPlayer) {
            //Player
            correctUsage =
                    Utils.createHoverEvent(
                            messages.getString("NORMAL.COMMAND.UNMUTE.GAME_CORRECT_USAGE"),
                            messages.getString("NORMAL.COMMAND.UNMUTE.HOVER_EVENT")
                    );
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerInfo server = player.getServer().getInfo();
            if (correctnessByGame(args, sender, correctUsage))
                unMutePlayer(server, sender, args[0]);
        } else {
            //Console
            correctUsage = new TextComponent(messages.getString("NORMAL.COMMAND.UNMUTE.CONSOLE_CORRECT_USAGE"));
            if (args.length != 0) {
                ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0]);
                if (correctnessByConsole(server, sender, args, correctUsage))
                    unMutePlayer(server, sender, args[1]);
            } else
                sender.sendMessage( correctUsage );
        }
    }

    private void unMutePlayer(ServerInfo server, CommandSender sender, String playerName) {
        if(!Utils.isUnderLimit(sender, messages, playerName))
            return;
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        Instant expiration;
        if(player != null)
            expiration = muteSystem.getPlayerMuteExpiration(server, player.getName());
        else
            expiration = muteSystem.getPlayerMuteExpiration(server, playerName);

        if (expiration == null)
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.UNMUTE.NOT_MUTED")));
        else {
            muteSystem.removePlayerMute(server, playerName);
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.UNMUTE.SUCCESS")));
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
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.WRONG_SERVER_NAME")));
                return false;
            }
            return true;
        } else sender.sendMessage( correctUsage );
        return false;
    }
}