package com.github.multidestroy.eventhandlers.listeners;

import com.github.multidestroy.Messages;
import com.github.multidestroy.MuteSystem;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.time.Instant;


public class MuteHandler implements Listener {

    private MuteSystem muteSystem;
    private Messages messages;

    public MuteHandler(MuteSystem muteSystem, Messages messages) {
        this.muteSystem = muteSystem;
        this.messages = messages;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        String message = event.getMessage();
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        ServerInfo server = player.getServer().getInfo();
        if (message.charAt(0) != '/') {
            if (muteSystem.getChatStatus(server)) {
                Instant expiration = muteSystem.getPlayerMuteExpiration(server, player.getName());
                if (expiration != null) {
                    player.sendMessage(messages.getMuteEvent(server, player.getName(), Instant.now()));
                    event.setCancelled(true);
                }
            } else {
                player.sendMessage(messages.getChatOFFEvent());
                event.setCancelled(true);
            }
        }
    }
}