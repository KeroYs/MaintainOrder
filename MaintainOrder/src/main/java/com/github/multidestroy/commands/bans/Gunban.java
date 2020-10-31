package com.github.multidestroy.commands.bans;

import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.environment.database.Database;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Gunban extends Unban {

    public Gunban(Database database, Messages messages) {
        super("gunban", CommandPermissions.gunban, database, messages);
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.GUNBAN.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.GUNBAN.HOVER_EVENT")
        );
    }

    protected String getServerName(ProxiedPlayer player) {
        return "bungeecord";
    }
}