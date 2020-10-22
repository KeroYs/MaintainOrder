package com.github.multidestroy.commands.kick;

import com.github.multidestroy.Config;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.SoundChannel;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.Utils;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Kick extends Command {
    private final Messages messages;
    private final Config config;

    public Kick(Messages messages, Config config) {
        super("kick", CommandPermissions.kick);
        this.messages = messages;
        this.config = config;
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
                    Utils.createHoverEvent(
                            messages.getString("NORMAL.COMMAND.KICK.GAME_CORRECT_USAGE"),
                            messages.getString("NORMAL.COMMAND.KICK.HOVER_EVENT")
                    );

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
            correctUsage = new TextComponent(messages.getString("NORMAL.COMMAND.KICK.CONSOLE_CORRECT_USAGE"));

            if (args.length < 3) {
                sender.sendMessage( correctUsage );
                return;
            }
            playerToKick = args[1];
            reason = Utils.mergeArray(args, 2);
            server = ProxyServer.getInstance().getServers().get(args[0]);
            if (server == null) {
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.WRONG_SERVER_NAME")));
                return;
            }
        }

        if(!Utils.isUnderLimit(sender, messages, playerToKick, reason))
            return;

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerToKick);
        if(player != null && player.getServer().getInfo().getName().equals(server.getName())) {
            //kick player

            SpecialTypeInfo specialTypeInfo = new SpecialTypeInfo();
            {
                specialTypeInfo.setReceiver(sender.getName());
                specialTypeInfo.setReason(reason);
                specialTypeInfo.setGiver(playerToKick);
            }
            player.disconnect(TextComponent.fromLegacyText(messages.getSpecialMessage(
                    SpecialType.COMMAND_KICK_RECEIVER,
                    specialTypeInfo
            )));
            Utils.sendGlobalMessage(server, TextComponent.fromLegacyText(messages.getSpecialMessage(
                    SpecialType.COMMAND_KICK_GLOBAL,
                    specialTypeInfo
            )));
            SoundChannel.sendServerSound(server, config.get().getString("sound.kick"));
        } else
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.PLAYER_OFFLINE")));
    }

}