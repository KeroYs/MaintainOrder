package com.github.multidestroy;

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

    private Map<String, Map<String, Instant>> muteBase;
    private Map<String, Boolean> chatStatus;

    MuteSystem() {
        createMuteBase();
    }

    private void createMuteBase() {
        muteBase = new HashMap<>();
        chatStatus = new HashMap<>();
        Set<String> keys = ProxyServer.getInstance().getServers().keySet();
        keys.forEach(currKey -> {
            muteBase.put(currKey, new HashMap<>());
            chatStatus.put(currKey, true);
        });
    }

    public boolean setChatStatus(ServerInfo server, boolean status) {
        chatStatus.put(server.getName(), status);
        return status;
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

    /**
     * @return null if player is not muted
     */

    public Instant getPlayerMuteExpiration(ServerInfo server, String playerName) {
        playerName = playerName.toLowerCase();
        Instant expiration = muteBase.get(server.getName()).get(playerName);
        if(expiration != null && expiration.isBefore(Instant.now())) {
            removePlayerMute(server, playerName);
            return null;
        }
        return expiration;
    }

    public void saveMutesToTheConfigurationFile(File dataFolder) {
        File configFile = new File(dataFolder, "temporary saved mutes.yml");
        Configuration configuration;
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            saveConfig(configuration);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
            configFile.setWritable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMutesFromTheConfigurationFile() {
        File configFile = new File(Main.dataFolder, "temporary saved mutes.yml");
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        try {
            if (configFile.exists()) {

                Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
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

}