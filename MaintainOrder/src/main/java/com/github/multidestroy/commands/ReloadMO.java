package com.github.multidestroy.commands;

import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ReloadMO extends Command {

    private final Messages messages;
    private final Database database;
    private final Config notificationsConfig;
    private PluginManager pluginManager;

    public ReloadMO(Messages messages, Database database, Config notificationsConfig, PluginManager pluginManager) {
        super("reload-mo", CommandPermissions.reload_mo);
        this.messages = messages;
        this.database = database;
        this.notificationsConfig = notificationsConfig;
        this.pluginManager = pluginManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        pluginManager.stopDeletingThreads(); //Stop actual running threads
        pluginManager.reloadConfigs();
        if (database.reloadDataSource()) {
            database.saveDefaultTables();
            messages.reloadFromConfig();
            pluginManager.registerDatabaseCommands();
            pluginManager.startDeletingThreads(); //Start deleting threads again
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.reload-mo.reload_status.good")));
        } else {
            pluginManager.unregisterDatabaseCommands();
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.reload-mo.reload_status.bad")));
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("commands.reload-mo.hint")));
        }
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }
}