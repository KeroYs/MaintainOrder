package com.github.multidestroy.commands;

import com.github.multidestroy.Config;
import com.github.multidestroy.Main;
import com.github.multidestroy.Messages;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;

public class ReloadMO extends Command {

    private final Messages messages;
    private final Database database;
    private final Config notificationsConfig;
    private final Main main;

    public ReloadMO(Messages messages, Database database, Main main, Config notificationsConfig) {
        super("reload-mo", CommandPermissions.reload_mo);
        this.messages = messages;
        this.database = database;
        this.notificationsConfig = notificationsConfig;
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        main.registerConfigs();
        if (main.isDataBaseInfoTyped()) {
            messages.reloadFromConfig();
            database.reloadDataSource();
            database.saveDefaultTables();
            if (!main.areCommandsRegistered())
                main.registerCommands();
            if (!main.areEventsRegistered())
                main.registerListeners();
            if (!main.areDeletingThreadsLaunched())
                main.startDeletingThreads();

            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.reload-mo.reload_status.good")));
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.reload-mo.reload_status.bad")));
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.reload-mo.hint")));
        }
    }


}
