package com.github.multidestroy.eventhandlers;

import com.github.multidestroy.Config;
import com.github.multidestroy.Main;
import com.github.multidestroy.Utils;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.info.BanData;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PlayerJoin implements Listener {

    private final Database database;
    private final Messages messages;
    private final Map<String, Boolean> playerMover;
    private final Config config;

    public PlayerJoin(Database database, Messages messages, Config config) {
        this.database = database;
        this.messages = messages;
        this.config = config;
        this.playerMover = new HashMap<>();
    }


    //Blacklist
    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if(database.isConnected()) {
            event.registerIntent(Main.plugin);
            switch (database.checkBan("blacklist", event.getConnection().getName(), event.getConnection().getSocketAddress().toString())) {
                case -1:
                    kickFromServer(event, TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                    break;
                case 1:
                    kickFromServer(event, TextComponent.fromLegacyText(messages.getSpecialMessage(
                            SpecialType.EVENT_GBAN,
                            new SpecialTypeInfo()
                    )));
                    break;
            }
            event.completeIntent(Main.plugin);
        } else {
            event.setCancelled(true);
            ProxyServer.getInstance().getPlayer(event.getConnection().getName()).sendMessage(
                    TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR"))
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
                                player.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
                                return;
                            case 1: {
                                SpecialTypeInfo specialTypeInfo = new SpecialTypeInfo();
                                BanData banData = database.getLastExpiringBan(event.getTarget().getName(), player.getName());

                                specialTypeInfo.setGiver(banData == null ? "ERROR" : banData.getGiverID());
                                specialTypeInfo.setReason(banData == null ? "ERROR" : banData.reason);
                                specialTypeInfo.setLeftTime((banData == null)? "ERROR" : (banData.isPerm())? "perm" : Utils.getLeftTimeAsString(Instant.now(), banData.getExpiration()));
                                specialTypeInfo.setExpirationDate((banData == null)? "ERROR" : (banData.isPerm())? "perm" : Utils.getLeftTimeAsString(Instant.now(), banData.getExpiration()));

                                player.sendMessage(TextComponent.fromLegacyText(messages.getSpecialMessage(
                                        SpecialType.EVENT_GBAN,
                                        specialTypeInfo
                                )));
                                return;
                            }
                        }
                        playerMover.put(player.getName(), true);
                        player.connect(target);
                    });
                } else playerMover.remove(player.getName());
            }
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                   TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")
            ));
        }
    }
}