package com.github.multidestroy.commands.bans;

import com.github.multidestroy.Main;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.info.ModificationType;
import com.github.multidestroy.info.BanData;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class GunBan extends Command {

    private final Database dataBase;
    private final Messages messages;

    public GunBan(Database dataBase, Messages messages) {
        super("gunban", CommandPermissions.gunban);
        this.dataBase = dataBase;
        this.messages = messages;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String playerName;
        String reason = "";

        TextComponent correctUsage =
                Utils.createHoverEvent(
                        messages.getString("NORMAL.COMMAND.GUNBAN.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.GUNBAN.HOVER_EVENT")
                );
        if(args.length < 1) {
            sender.sendMessage(correctUsage);
            return;
        } else {
            playerName = args[0];
            if(args.length > 1)
                reason = Utils.mergeArray(args, 1);
        }

        if(!Utils.isUnderLimit(sender, messages, playerName, reason))
            return;

        String finalReason = reason;
        Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> startAsync(sender, playerName, sender.getName(), finalReason));
    }

    private void startAsync(CommandSender sender, String playerName, String giverName, String reason) {
        BanData banData;
        switch (dataBase.checkBan("blacklist", playerName)) {
            case -1:
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                break;
            case 0:
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.GUNBAN.NOT_BANNED")));
                break;
            case 1:
                if(sender.hasPermission(CommandPermissions.gunbanop))
                    banData = dataBase.getLastGivenBan("blacklist", playerName);  //big_rank
                else
                    banData = dataBase.getLastGivenOwnBan("blacklist", playerName, giverName);//low_rank

                if(banData == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                    return;
                }

                if(dataBase.removeBan(sender, "blacklist", banData, ModificationType.UNBAN, reason))
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.GUNBAN.SUCCESS")));
                else
                    sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                break;
        }
    }
}