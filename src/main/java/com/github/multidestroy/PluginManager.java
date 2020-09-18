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
import com.github.multidestroy.eventhandlers.MuteHandler;
import com.github.multidestroy.eventhandlers.PlayerJoin;
import com.github.multidestroy.threads.DeleteExpiredBans;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PluginManager {

    private final List<Thread> ongoingDeletingThreads;
    private final Database database;
    private final Config config;
    private final Config messagesConfig;
    private final Config notificationsConfig;
    private final CommandsStructure commandsStructure;
    private final Plugin plugin;

    public PluginManager(Database database, Config config, Config messagesConfig, Config notificationsConfig,
                         CommandsStructure commandsStructure, Plugin plugin) {
        this.ongoingDeletingThreads = new ArrayList<>();
        this.database = database;
        this.config = config;
        this.messagesConfig = messagesConfig;
        this.notificationsConfig = notificationsConfig;
        this.commandsStructure = commandsStructure;
        this.plugin = plugin;
    }

    public void startDeletingThreads() {
        ProxyServer.getInstance().getServers().values().forEach(value -> {
            DeleteExpiredBans newThread = new DeleteExpiredBans(database, value.getName());
            newThread.start();
            ongoingDeletingThreads.add(newThread);
        });

        //blacklist
        DeleteExpiredBans newThread = new DeleteExpiredBans(database, "blacklist");
        newThread.start();
        ongoingDeletingThreads.add(newThread);
    }

    public void stopDeletingThreads() {
        ongoingDeletingThreads.forEach(Thread::interrupt);
        ongoingDeletingThreads.removeIf(next -> true);
    }

    public void reloadConfigs() {
        config.saveDefaultConfig();
        config.reloadCustomConfig();
        config.saveCustomConfig();

        messagesConfig.saveDefaultConfig();
        messagesConfig.reloadCustomConfig();

        notificationsConfig.saveDefaultConfig();
        notificationsConfig.reloadCustomConfig();
    }

    public void registerDatabaseCommands() {
        plugin.getProxy().getPluginManager().registerCommand(plugin, commandsStructure.ban);
        plugin.getProxy().getPluginManager().registerCommand(plugin, commandsStructure.gBan);
        plugin.getProxy().getPluginManager().registerCommand(plugin, commandsStructure.gunBan);
        plugin.getProxy().getPluginManager().registerCommand(plugin, commandsStructure.unBan);
        plugin.getProxy().getPluginManager().registerCommand(plugin, commandsStructure.info);
    }

    public void unregisterDatabaseCommands() {
        plugin.getProxy().getPluginManager().unregisterCommand(commandsStructure.ban);
        plugin.getProxy().getPluginManager().unregisterCommand(commandsStructure.gBan);
        plugin.getProxy().getPluginManager().unregisterCommand(commandsStructure.gunBan);
        plugin.getProxy().getPluginManager().unregisterCommand(commandsStructure.unBan);
        plugin.getProxy().getPluginManager().unregisterCommand(commandsStructure.info);
    }

}
