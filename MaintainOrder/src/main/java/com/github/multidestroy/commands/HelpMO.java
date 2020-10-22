package com.github.multidestroy.commands;

import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.time.Instant;

public class HelpMO extends Command {

    public Messages messages;

    public HelpMO(Messages messages) {
        super("help-mo", CommandPermissions.help_mo);
        this.messages = messages;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage =
                Utils.createHoverEvent(
                        messages.getString("NORMAL.COMMAND.HELP-MO.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.HELP-MO.HOVER_EVENT")
                );
        if (args.length == 0) {
            pageOne(sender, correctUsage);
        } else if (args.length == 1) {
            if (args[0].equals("1")) {
                pageOne(sender, correctUsage);
            } else if (args[0].equals("2")) {
                pageTwo(sender);
            } else
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.HELP-MO.PAGES_RANGE")));
        } else
            sender.sendMessage(correctUsage);
    }

    private void pageOne(CommandSender sender, TextComponent correctUsage) {
        Instant start = Instant.now();
        ComponentBuilder pageOne = new ComponentBuilder();
        pageOne.append(ChatColor.GRAY + "----- MaintainOrder help -----\n")
                .append(getPermissionInfoLine())
                .append(getCorrectUsageHelpMo(sender, correctUsage))
                .append(getCorrectUsageLine(sender, CommandPermissions.reload_mo,
                        messages.getString("NORMAL.COMMAND.RELOAD-MO.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.RELOAD-MO.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.info,
                        messages.getString("NORMAL.COMMAND.INFO.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.INFO.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.gban,
                        messages.getString("NORMAL.COMMAND.GBAN.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.GBAN.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.gunban, CommandPermissions.gunbanop,
                        messages.getString("NORMAL.COMMAND.GUNBAN.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.GUNBAN.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.gkick,
                        messages.getString("NORMAL.COMMAND.GKICK.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.GKICK.HOVER_EVENT")))
                .append(ChatColor.GRAY + "---------- Page 1 ----------");

        sender.sendMessage( pageOne.create() );
    }

    private void pageTwo(CommandSender sender) {
        Instant start = Instant.now();
        ComponentBuilder pageTwo = new ComponentBuilder();
        boolean isPlayer = sender instanceof ProxiedPlayer;
        pageTwo.append(ChatColor.GRAY + "----- MaintainOrder help -----\n")
                .append(getPermissionInfoLine())
                .append(getCorrectUsageLine(sender, CommandPermissions.ban,
                        isPlayer ? messages.getString("NORMAL.COMMAND.BAN.GAME_CORRECT_USAGE") : messages.getString("NORMAL.COMMAND.BAN.CONSOLE_CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.BAN.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.unban,
                        isPlayer ? messages.getString("NORMAL.COMMAND.UNBAN.GAME_CORRECT_USAGE") : messages.getString("NORMAL.COMMAND.UNBAN.CONSOLE_CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.UNBAN.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.kick,
                        isPlayer ? messages.getString("NORMAL.COMMAND.KICK.GAME_CORRECT_USAGE") : messages.getString("NORMAL.COMMAND.KICK.CONSOLE_CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.KICK.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.mute,
                        isPlayer ? messages.getString("NORMAL.COMMAND.MUTE.GAME_CORRECT_USAGE") : messages.getString("NORMAL.COMMAND.MUTE.CONSOLE_CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.MUTE.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.mutechat,
                        isPlayer ? messages.getString("NORMAL.COMMAND.MUTECHAT.GAME_CORRECT_USAGE") : messages.getString("NORMAL.COMMAND.MUTECHAT.CONSOLE_CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.MUTECHAT.HOVER_EVENT")))
                .append(getCorrectUsageLine(sender, CommandPermissions.unmute,
                        isPlayer ? messages.getString("NORMAL.COMMAND.UNMUTE.GAME_CORRECT_USAGE") : messages.getString("NORMAL.COMMAND.UNMUTE.CONSOLE_CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.UNMUTE.HOVER_EVENT")))
                .append(ChatColor.GRAY + "---------- Page 2 ----------");

        sender.sendMessage( pageTwo.create() );
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "two: " + ((float) (Instant.now().toEpochMilli() - start.toEpochMilli())) ));
    }

    private String getPermissionInfoLine() {
        return ChatColor.GREEN + messages.getString("NORMAL.COMMAND.HELP-MO.DISPLAY.HAVE_PERMISSION") +
                ChatColor.DARK_GRAY + " <> " +
                ChatColor.RED + messages.getString("NORMAL.COMMAND.HELP-MO.DISPLAY.LACK_OF_PERMISSION") + "\n";
    }

    private TextComponent getCorrectUsageLine(CommandSender sender, String permission, String correctUsageText, String hoverEventText) {
        TextComponent correctUsage = Utils.createHoverEvent(correctUsageText, hoverEventText);
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((sender.hasPermission(permission) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

    private TextComponent getCorrectUsageLine(CommandSender sender, String permission1, String permission2, String correctUsageText, String hoverEventText) {
        TextComponent correctUsage = Utils.createHoverEvent(correctUsageText, hoverEventText);
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((sender.hasPermission(permission1) || sender.hasPermission(permission2) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

    private TextComponent getCorrectUsageHelpMo(CommandSender sender, TextComponent correctUsage) {
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((sender.hasPermission(CommandPermissions.help_mo) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

}