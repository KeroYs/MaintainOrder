package com.github.multidestroy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;

public class SoundChannel {

    public static final String channel = "maintain_order:sound";
    public static final String localChannel = "SoundChannel";


    public static void sendServerSound(ServerInfo server, String soundEnumValueName) {
        Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();

        //check if there is at least one player in the game
        if (players.isEmpty())
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(localChannel);
        out.writeUTF(soundEnumValueName);

        server.sendData(channel, out.toByteArray());
    }

    public static void sendNetworkSound(String soundName) {
        ProxyServer.getInstance().getServers().values().forEach(currServer -> {
            sendServerSound(currServer, soundName);
        });
    }

}
