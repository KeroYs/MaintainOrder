package com.github.multidestroy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config {

    private Configuration config;
    private File configFile;
    private final String name;

    public Config(String name) {
        this.name = name;
    }

    public void saveDefaultConfig() {
        if(!Main.dataFolder.exists())
            Main.dataFolder.mkdir();

        if (configFile == null)
            configFile = new File(Main.dataFolder, name);

        if (!configFile.exists()) {
            try (InputStream in = Main.plugin.getResourceAsStream( name )) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveCustomConfig() {
        if(!Main.dataFolder.exists())
            Main.dataFolder.mkdir();

        if (configFile == null)
            configFile = new File(Main.dataFolder, name);

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadCustomConfig() {
        if(!Main.dataFolder.exists())
            Main.dataFolder.mkdir();

        if (configFile == null)
            configFile = new File(Main.dataFolder, name);

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration get() {
        return config;
    }

    public String getName() {
        return name;
    }

}