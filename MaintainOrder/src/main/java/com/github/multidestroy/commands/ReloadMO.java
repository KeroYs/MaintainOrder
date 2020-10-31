package com.github.multidestroy.commands;

import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.environment.CommandsManager;
import com.github.multidestroy.environment.database.Database;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

@Deprecated
public class ReloadMO extends Command {

    private final Messages messages;
    private final Database database;
    private CommandsManager commandsManager;

    public ReloadMO(Messages messages, Database database, CommandsManager commandsManager) {
        super("reload-mo", CommandPermissions.reload_mo);
        this.messages = messages;
        this.database = database;
        this.commandsManager = commandsManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        /*commandsManager.stopDeletingThreads(); //Stop actual running threads
        commandsManager.reloadConfigs();
        if (database.reloadDataSource()) {
            database.saveDefaultTables();
            commandsManager.registerDatabaseCommands();
            commandsManager.startDeletingThreads(); //Start deleting threads again
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.RELOAD-MO.RELOAD_STATUS.SUCCESS")));
        } else {
            commandsManager.unregisterDatabaseCommands();
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.RELOAD-MO.RELOAD_STATUS.ERROR")));
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.RELOAD-MO.RELOAD_STATUS.HINT")));
        }*/
    }
}