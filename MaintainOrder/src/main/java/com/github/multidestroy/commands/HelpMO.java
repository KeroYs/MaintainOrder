package com.github.multidestroy.commands;

import com.github.multidestroy.Config;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.commands.bans.Ban;
import com.github.multidestroy.commands.bans.GBan;
import com.github.multidestroy.commands.bans.GunBan;
import com.github.multidestroy.commands.bans.UnBan;
import com.github.multidestroy.commands.kick.GKick;
import com.github.multidestroy.commands.kick.Kick;
import com.github.multidestroy.commands.mute.Mute;
import com.github.multidestroy.commands.mute.MuteChat;
import com.github.multidestroy.commands.mute.UnMute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.time.Instant;
import java.util.Arrays;

public class HelpMO extends Command {

    public Config notificationsConfig;

    public HelpMO(Config notificationsConfig) {
        super("help-mo", CommandPermissions.help_mo);
        this.notificationsConfig = notificationsConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage =
                Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.help-mo.hover_event", "commands.help-mo.correct_usage");
        if (args.length == 0) {
            pageOne(sender, correctUsage);
        } else if (args.length == 1) {
            if (args[0].equals("1")) {
                pageOne(sender, correctUsage);
            } else if (args[0].equals("2")) {
                pageTwo(sender);
            } else
                sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.help-mo.pages_range")));;
        } else
            sender.sendMessage(correctUsage);
    }

    private void pageOne(CommandSender sender, TextComponent correctUsage) {
        Instant start = Instant.now();
        ComponentBuilder pageOne = new ComponentBuilder();
        pageOne.append(ChatColor.GRAY + "----- MaintainOrder help -----\n")
                .append(getPermissionInfoLine())
                .append(getCorrectUsageLine(sender, CommandPermissions.help_mo, correctUsage))
                .append(getCorrectUsageLine(sender, CommandPermissions.reload_mo, "commands.reload-mo.hover_event", "commands.reload-mo.correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.info, "commands.info.hover_event", "commands.info.correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.gban, "commands.gban.hover_event", "commands.gban.correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.gunban, CommandPermissions.gunbanop, "commands.gunban.hover_event", "commands.gunban.correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.gkick, "commands.gkick.hover_event", "commands.gkick.correct_usage"))
                .append(ChatColor.GRAY + "---------- Page 1 ----------");

        sender.sendMessage( pageOne.create() );
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "one: " + ((float) (Instant.now().toEpochMilli() - start.toEpochMilli())) ));
    }

    private void pageTwo(CommandSender sender) {
        Instant start = Instant.now();
        ComponentBuilder pageTwo = new ComponentBuilder();
        pageTwo.append(ChatColor.GRAY + "----- MaintainOrder help -----\n")
                .append(getPermissionInfoLine())
                .append(getCorrectUsageLine(sender, CommandPermissions.ban, "commands.ban.hover_event",
                        sender instanceof ProxiedPlayer ? "commands.ban.game_correct_usage" : "commands.ban.console_correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.unban, CommandPermissions.unbanop, "commands.unban.hover_event",
                        sender instanceof ProxiedPlayer ? "commands.unban.game_correct_usage" : "commands.unban.console_correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.kick, "commands.kick.hover_event",
                        sender instanceof ProxiedPlayer ? "commands.kick.game_correct_usage" : "commands.kick.console_correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.mute, "commands.mute.hover_event",
                        sender instanceof ProxiedPlayer ? "commands.mute.game_correct_usage" : "commands.mute.console_correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.mutechat, "commands.mutechat.hover_event",
                        sender instanceof ProxiedPlayer ? "commands.mutechat.game_correct_usage" : "commands.mutechat.console_correct_usage"))
                .append(getCorrectUsageLine(sender, CommandPermissions.unmute, "commands.unmute.hover_event",
                        sender instanceof ProxiedPlayer ? "commands.unmute.game_correct_usage" : "commands.unmute.console_correct_usage"))
                .append(ChatColor.GRAY + "---------- Page 2 ----------");

        sender.sendMessage( pageTwo.create() );
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "two: " + ((float) (Instant.now().toEpochMilli() - start.toEpochMilli())) ));
    }

    private String getPermissionInfoLine() {
        return ChatColor.GREEN + notificationsConfig.get().getString("commands.help-mo.permissions.have") +
                ChatColor.DARK_GRAY + " <> " +
                ChatColor.RED + notificationsConfig.get().getString("commands.help-mo.permissions.not_have") + "\n";
    }

    private TextComponent getCorrectUsageLine(CommandSender sender, String permission, String hoverEventPath, String correctUsagePath) {
        TextComponent correctUsage = Utils.createHoverEvent_OneDesc(notificationsConfig, hoverEventPath, correctUsagePath);
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((sender.hasPermission(permission) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

    private TextComponent getCorrectUsageLine(CommandSender sender, String permission1, String permission2, String hoverEventPath, String correctUsagePath) {
        TextComponent correctUsage = Utils.createHoverEvent_TwoDesc(notificationsConfig, hoverEventPath, correctUsagePath);
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((sender.hasPermission(permission1) || sender.hasPermission(permission2) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

    private TextComponent getCorrectUsageLine(CommandSender sender, String permission, TextComponent correctUsage) {
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((sender.hasPermission(permission) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

}