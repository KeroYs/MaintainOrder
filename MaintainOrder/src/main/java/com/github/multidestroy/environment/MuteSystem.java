package com.github.multidestroy.environment;

import com.github.multidestroy.MainPluginClass;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MuteSystem {

    private final Map<String, Map<String, Instant>> muteBase; //Expiration time for each player
    private final Map<String, Boolean> chatStatus; //Chat statuses for each server

    public MuteSystem() {
        muteBase = new HashMap<>();
        chatStatus = new HashMap<>();
        setServers();
    }

    private void setServers() {
        Set<String> keys = ProxyServer.getInstance().getServers().keySet();
        keys.forEach(server -> {
            muteBase.put(server, new HashMap<>());
            chatStatus.put(server, true);
        });
    }

    public void setChatStatus(ServerInfo server, boolean status) {
        chatStatus.put(server.getName(), status);
    }

    public boolean getChatStatus(ServerInfo server) {
        return chatStatus.get(server.getName());
    }

    public void givePlayerMute(ServerInfo server, String playerName, Instant expiration) {
        muteBase.get(server.getName()).put(playerName.toLowerCase(), expiration);
    }

    public void removePlayerMute(ServerInfo server, String playerName) {
        muteBase.get(server.getName()).remove(playerName.toLowerCase());
    }

    public boolean isPlayerMuted(ServerInfo server, String playerName) {
        return getPlayerMuteExpiration(server, playerName) != Instant.EPOCH;
    }

    public Instant getPlayerMuteExpiration(ServerInfo server, String playerName) {
        Instant expiration = muteBase.get(server.getName()).getOrDefault(playerName.toLowerCase(), Instant.EPOCH);

        if (isMuteExpired(expiration)) {
            removePlayerMute(server, playerName);
            return Instant.EPOCH;
        }

        return expiration;
    }

    private boolean isMuteExpired(Instant expiration) {
        return expiration.isBefore(Instant.now());
    }

    public void saveMutesToTheTemporaryFile(File dataFolder) {
        File configFile = new File(dataFolder, "mutes.yml");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            saveConfig(configuration);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
            configFile.setWritable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMutesFromTheConfigurationFile() {
        File configFile = new File(MainPluginClass.dataFolder, "mutes.yml");
        try {
            if (configFile.exists()) {

                Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
                assignMutes(configuration);
                configFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveConfig(Configuration configuration) {
        muteBase.forEach((serverName, mutes) -> {
            mutes.forEach((playerName, expiration) -> {
                configuration.getSection(serverName).getSection(playerName).set("expiration", expiration.toEpochMilli());
            });
        });
    }

    private void assignMutes(Configuration configuration) {
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        //servers
        configuration.getKeys().forEach(section -> {
            ServerInfo server = servers.get(section);
            if(server != null) {
                //players
                configuration.getSection(section).getKeys().forEach(player -> {
                    givePlayerMute(server, player, Instant.ofEpochMilli(configuration.getSection(section).getSection(player).getLong("expiration")));
                });
            }
        });
    }

}