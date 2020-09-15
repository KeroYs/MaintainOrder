package com.github.multidestroy.commands.mute;

import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.CommandPermissions;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MuteChat extends Command {

    private final MuteSystem muteSystem;
    private final Messages messages;
    private final Config config;
    private final Config notificationsConfig;

    public MuteChat(MuteSystem muteSystem, Messages messages, Config config, Config notificationsConfig) {
        super("mutechat", CommandPermissions.mutechat);
        this.muteSystem = muteSystem;
        this.messages = messages;
        this.config = config;
        this.notificationsConfig = notificationsConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ServerInfo server;
        TextComponent correctUsage;
        int statusIndex;
        if (sender instanceof ProxiedPlayer) {
            //Player
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.mutechat.hover_event", "commands.mutechat.game_correct_usage");
            server = ((ProxiedPlayer) sender).getServer().getInfo();
            statusIndex = 0;
        } else {
            //Console
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.mutechat.hover_event", "commands.mutechat.console_correct_usage");
            if (args.length != 0) {
                server = ProxyServer.getInstance().getServers().get(args[0]);
                if (server == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.wrong_server_name")));
                    return;
                }
                statusIndex = 1;

            } else {
                sender.sendMessage( correctUsage );
                return;
            }
        }
        switch (start(sender, args, statusIndex, server, correctUsage)) {
            case 0:
                Utils.sendGlobalMessage(server, messages.getMuteChatOFF(sender.getName()));
                break;
            case 1:
                Utils.sendGlobalMessage(server, messages.getMuteChatON(sender.getName()));
                break;
        }
        SoundChannel.sendServerSound(server, config.get().getString("sound.mutechat"));
    }

    /**
     * @return -1: wrong use of the command, 0: chat is OFF, 1: chat is ON
     */

    private int start(CommandSender sender, String[] args, int statusIndex, ServerInfo server, TextComponent correctUsage) {
        if (args.length == statusIndex) {
            if(muteSystem.setChatStatus(server, !muteSystem.getChatStatus(server)))
                return 1;
            return 0;
        } else if (args.length == statusIndex + 1) {
            String status = args[statusIndex];
            if (status.equalsIgnoreCase("on")) {
                if(muteSystem.getChatStatus(server)) {
                    sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.mutechat.same_status")));
                    return -1;
                }
                muteSystem.setChatStatus(server, true);
                return 1;
            } else if (status.equalsIgnoreCase("off")) {
                if(!muteSystem.getChatStatus(server)) {
                    sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.mutechat.same_status")));
                    return -1;
                }
                muteSystem.setChatStatus(server, false);
                return 0;
            }
            else {
                sender.sendMessage(correctUsage);
                return -1;
            }
        } else {
            sender.sendMessage(correctUsage);
            return -1;
        }
    }
}
