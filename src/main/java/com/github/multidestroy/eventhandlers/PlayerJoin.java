package com.github.multidestroy.eventhandlers;

import com.github.multidestroy.Config;
import com.github.multidestroy.Main;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class PlayerJoin implements Listener {

    private final Database database;
    private final Messages messages;
    private final Map<String, Boolean> playerMover;
    private final Config config;
    private final Config notificationsConfig;

    public PlayerJoin(Database database, Messages messages, Config config, Config notificationsConfig) {
        this.database = database;
        this.messages = messages;
        this.config = config;
        this.playerMover = new HashMap<>();
        this.notificationsConfig = notificationsConfig;
    }


    //Blacklist
    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if(database.isConnected()) {
            event.registerIntent(Main.plugin);
            switch (database.checkBan("blacklist", event.getConnection().getName(), event.getConnection().getSocketAddress().toString())) {
                case -1:
                    kickFromServer(event, TextComponent.fromLegacyText(notificationsConfig.get().getString("database.error")));
                    break;
                case 0: {
                    switch (database.checkIpBlockade(event.getConnection().getName(), ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress())) {
                        case -1:
                            kickFromServer(event, TextComponent.fromLegacyText(notificationsConfig.get().getString("database.error")));
                            break;
                        case 1:
                            kickFromServer(event, messages.getIpBlockadeEvent());
                            break;
                    }
                    break;
                }
                case 1:
                    kickFromServer(event, messages.getGBanEvent("blacklist", event.getConnection().getName()));
                    break;
            }
            event.completeIntent(Main.plugin);
        } else {
            event.setCancelled(true);
            ProxyServer.getInstance().getPlayer(event.getConnection().getName()).sendMessage(
                    TextComponent.fromLegacyText(notificationsConfig.get().getString("database.error"))
            );
        }
    }

    private void kickFromServer(PreLoginEvent event, BaseComponent[] message) {
        event.setCancelReason(message);
        event.setCancelled(true);
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (database.isConnected()) {
            if (!event.getTarget().getName().equalsIgnoreCase(config.get().getString("lobby_server_name"))) {
                ServerInfo target = event.getTarget();
                ProxiedPlayer player = event.getPlayer();
                if (playerMover.get(player.getName()) == null) {
                    event.setCancelled(true);
                    Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> {
                        switch (database.checkBan(event.getTarget().getName(), player.getName(), player.getSocketAddress().toString())) {
                            case -1:
                                player.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("database.error")));
                                return;
                            case 1:
                                player.sendMessage(messages.getBanEvent(event.getTarget().getName(), player.getName()));
                                return;
                        }
                        playerMover.put(player.getName(), true);
                        player.connect(target);
                    });
                } else playerMover.remove(player.getName());
            }
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    TextComponent.fromLegacyText(notificationsConfig.get().getString("database.error"))
            );
        }
    }
}
