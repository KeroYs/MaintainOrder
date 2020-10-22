package com.github.multidestroy.commands.bans;

import com.github.multidestroy.Main;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.info.ModificationType;
import com.github.multidestroy.info.BanData;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnBan extends Command {

    private final Database dataBase;
    private final Messages messages;

    public UnBan(Database dataBase, Messages messages) {
        super("unban", CommandPermissions.unban);
        this.dataBase = dataBase;
        this.messages = messages;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ServerInfo server;
        String playerName;
        TextComponent correctUsage;
        String reason = "";
        if(sender instanceof ProxiedPlayer) {
            correctUsage =
                    Utils.createHoverEvent(
                            messages.getString("NORMAL.COMMAND.UNBAN.GAME_CORRECT_USAGE"),
                            messages.getString("NORMAL.COMMAND.UNBAN.HOVER_EVENT")
                    );
            if(args.length < 1) {
                sender.sendMessage( correctUsage );
                return;
            } else {
                server = ((ProxiedPlayer) sender).getServer().getInfo();
                playerName = args[0];
                if(args.length > 1)
                    reason = Utils.mergeArray(args, 1);
            }
        } else {
            correctUsage =
                    new TextComponent(messages.getString("NORMAL.COMMAND.UNBAN.CONSOLE_CORRECT_USAGE"));
            if(args.length < 2) {
                sender.sendMessage( correctUsage );
                return;
            } else {
                server = ProxyServer.getInstance().getServerInfo(args[0]);
                if(server == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.WRONG_SERVER_NAME")));
                    return;
                }
                playerName = args[1];
                if(args.length > 2)
                    reason = Utils.mergeArray(args, 2);
            }
        }

        if(!Utils.isUnderLimit(sender, messages, playerName, reason))
            return;

        String finalReason = reason;
        Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> startAsync(sender, server, playerName, sender.getName(), finalReason));
    }

    private void startAsync(CommandSender sender, ServerInfo server, String playerName, String giverName, String reason) {
        BanData banData;
        switch (dataBase.checkBan(server.getName(), playerName)) {
            case -1:
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                break;
            case 0:
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.UNBAN.NOT_BANNED")));
                break;
            case 1:
                if(sender.hasPermission(CommandPermissions.unbanop))
                    banData = dataBase.getLastGivenBan(server.getName(), playerName); //big_rank
                else
                    banData = dataBase.getLastGivenOwnBan(server.getName(), playerName, giverName); //low_rank

                if(banData == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                    return;
                }

                if(dataBase.removeBan(sender, server.getName(), banData, ModificationType.UNBAN, reason))
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.UNBAN.SUCCESS")));
                else
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                break;
        }
    }
}