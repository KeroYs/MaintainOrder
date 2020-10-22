package com.github.multidestroy.commands;

import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ReloadMO extends Command {

    private final Messages messages;
    private final Database database;
    private PluginManager pluginManager;

    public ReloadMO(Messages messages, Database database, PluginManager pluginManager) {
        super("reload-mo", CommandPermissions.reload_mo);
        this.messages = messages;
        this.database = database;
        this.pluginManager = pluginManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        pluginManager.stopDeletingThreads(); //Stop actual running threads
        pluginManager.reloadConfigs();
        if (database.reloadDataSource()) {
            database.saveDefaultTables();
            pluginManager.registerDatabaseCommands();
            pluginManager.startDeletingThreads(); //Start deleting threads again
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.RELOAD-MO.RELOAD_STATUS.SUCCESS")));
        } else {
            pluginManager.unregisterDatabaseCommands();
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.RELOAD-MO.RELOAD_STATUS.ERROR")));
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.RELOAD-MO.RELOAD_STATUS.HINT")));
        }
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }
}