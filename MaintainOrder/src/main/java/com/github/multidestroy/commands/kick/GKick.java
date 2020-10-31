package com.github.multidestroy.commands.kick;

import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GKick extends Kick {

    public GKick(Messages messages) {
        super("gkick", CommandPermissions.gkick, messages);
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.GKICK.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.GKICK.HOVER_EVENT")
        );
    }

    @Override
    protected boolean canKickPlayer(ProxiedPlayer player1, ProxiedPlayer player2) {
        return player1.isConnected() && player2.isConnected();
    }

    @Override
    protected void sendGlobalMessage(ServerInfo server, BaseComponent[] message) {
        ProxyServer.getInstance().getServers().values().forEach(serverInfo -> {
            serverInfo.getPlayers().forEach(player -> player.sendMessage(message));
        });
    }
}