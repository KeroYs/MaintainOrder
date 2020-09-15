package com.github.multidestroy;
import java.lang.instrument.Instrumentation;
import com.github.multidestroy.commands.Gungan;
import com.github.multidestroy.commands.HelpMO;
import com.github.multidestroy.commands.Info;
import com.github.multidestroy.commands.ReloadMO;
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
import com.github.multidestroy.eventhandlers.MuteHandler;
import com.github.multidestroy.eventhandlers.PlayerJoin;
import com.github.multidestroy.info.PlayerRank;
import com.github.multidestroy.threads.DeleteExpiredBans;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

//TODO: blacklist not defined
public class Main extends Plugin {

    public static File dataFolder;
    public static Plugin plugin;
    private Config config;
    private Config messagesConfig;
    private Config notificationsConfig;
    private Database database;
    private MuteSystem muteSystem;
    private Messages messages;
    private boolean commandsRegisterStatus;
    private boolean eventsRegisterStatus;
    private boolean deletingThreadsStatus;


    @Override
    public void onEnable() {
        Instant start = Instant.now();
        commandsRegisterStatus = false;
        eventsRegisterStatus = false;
        deletingThreadsStatus = false;
        dataFolder = getDataFolder();
        plugin = this;
        Locale locale = new Locale("pl", "PL");
        locale.get

        registerConfigs();
        PlayerRank playerRank = new PlayerRank(config);
        database = new Database(config, playerRank);
        (muteSystem = new MuteSystem()).readMutesFromTheConfigurationFile();
        messages = new Messages(messagesConfig, database, muteSystem);
        getProxy().getPluginManager().registerCommand(this, new ReloadMO(messages, database, this, notificationsConfig));
        if(isDataBaseInfoTyped()) {
            getProxy().registerChannel(SoundChannel.channel);

            database.reloadDataSource();
            database.saveDefaultTables();
            startDeletingThreads();
            registerCommands();
            registerListeners();
            getLogger().info(ChatColor.GREEN + "Launched in: " + ((float) (Instant.now().toEpochMilli() - start.toEpochMilli())) / 1000 + " s");
        } else {
            getLogger().warning(ChatColor.RED + "Plugin was not loaded!");
            getLogger().warning(ChatColor.GOLD + "Complete the database section in MaintainOrder/config.yml and then insert /reload-mo command");
        }
    }

    @Override
    public void onDisable() {
        muteSystem.saveMutesToTheConfigurationFile(getDataFolder());
    }

    public void startDeletingThreads() {
        ProxyServer.getInstance().getServers().values().forEach(value -> {
            DeleteExpiredBans newThread = new DeleteExpiredBans(database, value.getName());
            newThread.start();
        });

        //blacklist
        DeleteExpiredBans newThread = new DeleteExpiredBans(database, "blacklist");
        newThread.start();
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

    public void registerCommands() {
        commandsRegisterStatus = true;
        getProxy().getPluginManager().registerCommand(this, new Ban(database, messages, config, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new GBan(database, messages, config, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new UnBan(database, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new GunBan(database, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new Kick(messages, config, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new GKick(messages, config, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new MuteChat(muteSystem, messages, config, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new Mute(muteSystem, messages, config, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new UnMute(muteSystem, notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new HelpMO(notificationsConfig));
        getProxy().getPluginManager().registerCommand(this, new Gungan(config));
        getProxy().getPluginManager().registerCommand(this, new Info(muteSystem, database, notificationsConfig));
    }

    public void registerListeners() {
        eventsRegisterStatus = true;
        getProxy().getPluginManager().registerListener(this, new PlayerJoin(database, messages, config, notificationsConfig));
        getProxy().getPluginManager().registerListener(this, new MuteHandler(muteSystem, messages));

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

    public boolean isDataBaseInfoTyped() {
        String tmp;
        if((tmp = config.get().getString("database.host")) != null && tmp.length() != 0)
            if((tmp = config.get().getString("database.port")) != null && tmp.length() != 0)
                if((tmp = config.get().getString("database.name")) != null && tmp.length() != 0)
                    if((tmp = config.get().getString("database.username")) != null && tmp.length() != 0)
                        return (tmp = config.get().getString("database.password")) != null && tmp.length() != 0;
        return false;
    }

    public boolean areCommandsRegistered() {
        return commandsRegisterStatus;
    }

    public boolean areEventsRegistered() {
        return eventsRegisterStatus;
    }

    public boolean areDeletingThreadsLaunched() {
        return deletingThreadsStatus;
    }
}