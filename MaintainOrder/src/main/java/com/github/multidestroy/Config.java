package com.github.multidestroy;

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
        if(!MainPluginClass.dataFolder.exists())
            MainPluginClass.dataFolder.mkdir();

        if (configFile == null)
            configFile = new File(MainPluginClass.dataFolder, name);

        if (!configFile.exists()) {
            try (InputStream in = MainPluginClass.plugin.getResourceAsStream( name )) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveCustomConfig() {
        if(!MainPluginClass.dataFolder.exists())
            MainPluginClass.dataFolder.mkdir();

        if (configFile == null)
            configFile = new File(MainPluginClass.dataFolder, name);

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadCustomConfig() {
        if(!MainPluginClass.dataFolder.exists())
            MainPluginClass.dataFolder.mkdir();

        if (configFile == null)
            configFile = new File(MainPluginClass.dataFolder, name);

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