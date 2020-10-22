package com.github.multidestroy.eventhandlers;

import com.github.multidestroy.MuteSystem;
import com.github.multidestroy.Utils;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.chat.TextComponent;
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
        SpecialTypeInfo specialTypeInfo = new SpecialTypeInfo();
        if (message.charAt(0) != '/') {
            if (muteSystem.getChatStatus(server)) {
                Instant expiration = muteSystem.getPlayerMuteExpiration(server, player.getName());
                if (expiration != null) {
                    specialTypeInfo.setLeftTime(
                            Utils.getLeftTimeAsString(Instant.now(), muteSystem.getPlayerMuteExpiration(server, player.getName()))
                    );
                    player.sendMessage(TextComponent.fromLegacyText(messages.getSpecialMessage(
                            SpecialType.EVENT_MUTE,
                            specialTypeInfo
                    )));
                    event.setCancelled(true);
                }
            } else {
                player.sendMessage(TextComponent.fromLegacyText(messages.getSpecialMessage(
                        SpecialType.EVENT_CHATOFF,
                        specialTypeInfo
                )));
                event.setCancelled(true);
            }
        }
    }
}