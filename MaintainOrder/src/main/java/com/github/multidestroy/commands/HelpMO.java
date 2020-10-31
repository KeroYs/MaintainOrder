package com.github.multidestroy.commands;

import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.environment.CommandsManager;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.time.Instant;

public class HelpMO extends MaintainOrderCommand {

    private CommandsManager commandsManager;

    public HelpMO(Messages messages, CommandsManager commandsManager) {
        super(messages, "help-mo", CommandPermissions.help_mo, 0, 1);
        this.commandsManager = commandsManager;
    }

    @Override
    public void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        BaseComponent[] page = getPage(executor, args);

        executor.sendMessage(page);
    }

    private BaseComponent[] getPage(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("1"))
                return pageOne(executor);
            if (args[0].equalsIgnoreCase("2"))
                return pageTwo(executor);

            throw new WrongArgumentException(ChatColor.RED + "Pages: [1, 2]");
        }
        return pageOne(executor);
    }


    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.HELP-MO.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.HELP-MO.HOVER_EVENT")
        );
    }

    private BaseComponent[] pageOne(ProxiedPlayer executor) {
        ComponentBuilder page = new ComponentBuilder();
        page.append(ChatColor.GRAY + "-----[ MaintainOrder help ]-----\n")
                .append(getPermissionInfoLine())
                .append(getCorrectUsageLine(executor, CommandPermissions.help_mo, getCorrectUsage()))
//                .append(getCorrectUsageLine(executor, CommandPermissions.reload_mo, commandsManager.get("reload_mo").getCorrectUsage()))
//                .append(getCorrectUsageLine(sender, CommandPermissions.info, commandsManager.get("info").getCorrectUsage())
                .append(getCorrectUsageLine(executor, CommandPermissions.gban, commandsManager.get("gban").getCorrectUsage()))
                .append(getCorrectUsageLine(executor, CommandPermissions.ban, commandsManager.get("ban").getCorrectUsage()))
                .append(getCorrectUsageLine(executor, CommandPermissions.gunban, CommandPermissions.gunbanop, commandsManager.get("gunban").getCorrectUsage()))
                .append(getCorrectUsageLine(executor, CommandPermissions.unban, CommandPermissions.unbanop, commandsManager.get("unban").getCorrectUsage()))
                .append(ChatColor.GRAY + "----------[ Page 1 ]----------");

        return page.create();
    }

    private BaseComponent[] pageTwo(ProxiedPlayer executor) {
        ComponentBuilder page = new ComponentBuilder();
        page.append(ChatColor.GRAY + "-----[ MaintainOrder help ]-----\n")
                .append(getPermissionInfoLine())
                .append(getCorrectUsageLine(executor, CommandPermissions.gkick, commandsManager.get("gkick").getCorrectUsage()))
                .append(getCorrectUsageLine(executor, CommandPermissions.kick, commandsManager.get("kick").getCorrectUsage()))
                .append(getCorrectUsageLine(executor, CommandPermissions.mute, commandsManager.get("mute").getCorrectUsage()))
                .append(getCorrectUsageLine(executor, CommandPermissions.unmute, commandsManager.get("unmute").getCorrectUsage()))
                .append(getCorrectUsageLine(executor, CommandPermissions.mutechat, commandsManager.get("mutechat").getCorrectUsage()))
                .append(ChatColor.GRAY + "----------[ Page 2 ]----------");

       return page.create();
    }

    private String getPermissionInfoLine() {
        return ChatColor.GREEN + messages.getString("NORMAL.COMMAND.HELP-MO.DISPLAY.HAVE_PERMISSION") +
                ChatColor.DARK_GRAY + " <> " +
                ChatColor.RED + messages.getString("NORMAL.COMMAND.HELP-MO.DISPLAY.LACK_OF_PERMISSION") + "\n";
    }

    private TextComponent getCorrectUsageLine(ProxiedPlayer executor, String permission, TextComponent correctUsage) {
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((executor.hasPermission(permission) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

    private TextComponent getCorrectUsageLine(ProxiedPlayer executor, String permission1, String permission2, TextComponent correctUsage) {
        HoverEvent hoverEvent = correctUsage.getHoverEvent();
        TextComponent newLine = new TextComponent((executor.hasPermission(permission1) || executor.hasPermission(permission2) ? ChatColor.GREEN : ChatColor.RED) +
                ChatColor.stripColor(correctUsage.getText()) + "\n");

        newLine.setHoverEvent(hoverEvent);

        return newLine;
    }

}