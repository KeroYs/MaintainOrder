package com.github.multidestroy;
import com.github.multidestroy.commands.Gungan;
import com.github.multidestroy.commands.HelpMO;
import com.github.multidestroy.commands.Info;
import com.github.multidestroy.commands.ReloadMO;
import com.github.multidestroy.commands.assets.CommandsStructure;
import com.github.multidestroy.commands.bans.Ban;
import com.github.multidestroy.commands.bans.GBan;
import com.github.multidestroy.commands.bans.GunBan;
import com.github.multidestroy.commands.bans.UnBan;
import com.github.multidestroy.commands.kick.GKick;
import com.github.multidestroy.commands.kick.Kick;
import com.github.multidestroy.commands.mute.Mute;
import com.github.multidestroy.commands.mute.MuteChat;
import com.github.multidestroy.commands.mute.UnMute;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.events.listeners.MuteHandler;
import com.github.multidestroy.events.listeners.PlayerJoin;
import com.github.multidestroy.info.PlayerRank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.time.Instant;
import java.util.*;

public class Main extends Plugin {

    public static File dataFolder;
    public static Plugin plugin;
    private Config config;
    private Config messagesConfig;
    private Config notificationsConfig;
    private Database database;
    private MuteSystem muteSystem;
    private Messages messages;

    @Override
    public void onEnable() {
        Instant start = Instant.now();
        dataFolder = getDataFolder();
        plugin = this;

        registerConfigs();
        PlayerRank playerRank = new PlayerRank(config);
        database = new Database(config, playerRank);
        (muteSystem = new MuteSystem()).readMutesFromTheConfigurationFile();
        messages = new Messages(messagesConfig, database, muteSystem);
        CommandsStructure commandsStructure = createCommandsObjects(); //Store all object of commands classes

        /* Create plugin manager */

        PluginManager pluginManager = new PluginManager(database, config, messagesConfig, notificationsConfig, commandsStructure, plugin);

        /* Those commands below are always ON (they do not depend on database) */

        commandsStructure.reloadMO.setPluginManager(pluginManager);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.reloadMO);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.helpMO);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.gungan);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.mute);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.muteChat);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.unMute);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.gKick);
        getProxy().getPluginManager().registerCommand(this, commandsStructure.kick);

        /* Register listeners */

        getProxy().getPluginManager().registerListener(this, new PlayerJoin(database, messages, config, notificationsConfig));
        getProxy().getPluginManager().registerListener(this, new MuteHandler(muteSystem, messages));

        /* Register plugin channel to send sounds to bukkit server */

        getProxy().registerChannel(SoundChannel.channel);



        if (database.reloadDataSource()) {
            //If plugin has connected with database
            database.saveDefaultTables();
            messages.reloadFromConfig();
            pluginManager.startDeletingThreads();
            pluginManager.registerDatabaseCommands();
            getLogger().info(ChatColor.GREEN + "Launched in: " + ((float) (Instant.now().toEpochMilli() - start.toEpochMilli())) / 1000 + " s");
        } else {
            getLogger().warning(ChatColor.RED + "Plugin was not loaded!");
            getLogger().warning(ChatColor.YELLOW + "Check out your data typed in plugins/MaintainOrder/config.yml");
        }

    }

    @Override
    public void onDisable() {
        muteSystem.saveMutesToTheConfigurationFile(getDataFolder());
    }

    public void registerConfigs() {
        config = new Config("config.yml");
        config.saveDefaultConfig();
        config.reloadCustomConfig();
        saveServersInConfig();
        config.saveCustomConfig();

        messagesConfig = new Config("messages.yml");
        messagesConfig.saveDefaultConfig();
        messagesConfig.reloadCustomConfig();

        notificationsConfig = new Config("notifications.yml");
        notificationsConfig.saveDefaultConfig();
        notificationsConfig.reloadCustomConfig();
    }

    private void saveServersInConfig() {
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        if (config.get().getString("server.blacklist") == null ||
                config.get().getString("server.blacklist").length() == 0)
            config.get().set("server.blacklist", "blacklist");

        servers.values().forEach(server -> {
            if (config.get().getSection("server").getString(server.getName()) == null ||
                    config.get().getSection("server").getString(server.getName()).length() == 0)
                config.get().getSection("server").set(server.getName(), "default_table");
        });
    }

    private CommandsStructure createCommandsObjects() {
        return new CommandsStructure (
                new Ban(database, messages, config, notificationsConfig),
                new GBan(database, messages, config, notificationsConfig),
                new GunBan(database, notificationsConfig),
                new UnBan(database, notificationsConfig),
                new GKick(messages, config, notificationsConfig),
                new Kick(messages, config, notificationsConfig),
                new Mute(muteSystem, messages, config, notificationsConfig),
                new MuteChat(muteSystem, messages, config, notificationsConfig),
                new UnMute(muteSystem, notificationsConfig),
                new Gungan(config),
                new HelpMO(notificationsConfig),
                new Info(muteSystem, database, notificationsConfig),
                new ReloadMO(messages, database, notificationsConfig, null));
    }

}