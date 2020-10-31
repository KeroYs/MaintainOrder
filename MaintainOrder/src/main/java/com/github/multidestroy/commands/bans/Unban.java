package com.github.multidestroy.commands.bans;

import com.github.multidestroy.MainPluginClass;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.environment.database.Database;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;

public class Unban extends MaintainOrderCommand {

    private final Database database;

    public Unban(Database database, Messages messages) {
        super(messages,"unban", CommandPermissions.unban, 2);
        this.database = database;
    }

    protected Unban(String commandName, String permission, Database database, Messages messages) {
        super(messages, commandName, permission, 2);
        this.database = database;
    }


    @Override
    public final void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        String recipient = getPlayer(args[0]);
        String reason = getReason(args, 1);
        String serverName = getServerName(executor);

        MainPluginClass.plugin.getProxy().getScheduler().runAsync(
                MainPluginClass.plugin,
                () -> unbanPlayer(executor, recipient, serverName, reason)
        );

    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.UNBAN.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.UNBAN.HOVER_EVENT")
        );
    }

    private void unbanPlayer(ProxiedPlayer giver, String recipient, String serverName, String reason) {
        try {
            database.unbanPlayer(giver.getName(), recipient, serverName, reason);
        } catch (SQLException ex) {

        }
    }

    protected String getServerName(ProxiedPlayer player) {
        return player.getServer().getInfo().getName();
    }
}