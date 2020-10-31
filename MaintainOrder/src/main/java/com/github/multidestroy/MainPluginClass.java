package com.github.multidestroy;

import com.github.multidestroy.commands.Gungan;
import com.github.multidestroy.commands.HelpMO;
import com.github.multidestroy.commands.bans.Ban;
import com.github.multidestroy.commands.bans.GBan;
import com.github.multidestroy.commands.bans.Gunban;
import com.github.multidestroy.commands.bans.Unban;
import com.github.multidestroy.commands.kick.GKick;
import com.github.multidestroy.commands.kick.Kick;
import com.github.multidestroy.commands.mute.Mute;
import com.github.multidestroy.commands.mute.MuteChat;
import com.github.multidestroy.commands.mute.UnMute;
import com.github.multidestroy.environment.CommandsManager;
import com.github.multidestroy.environment.MuteSystem;
import com.github.multidestroy.environment.SoundChannel;
import com.github.multidestroy.environment.database.Database;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.info.PlayerRank;
import com.github.multidestroy.listeners.MuteHandler;
import com.github.multidestroy.listeners.PlayerJoin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.time.Instant;
import java.util.Locale;

public class MainPluginClass extends Plugin {

    public static File dataFolder;
    public static Plugin plugin;
    private Config config;
    private Database database;
    private MuteSystem muteSystem;
    private Messages messages;

    @Override
    public void onEnable() {
        //TODO FileSystem.newFileSystem()
        //ExpiredBansRemover newThread = new ExpiredBansRemover(database, "blacklist");
        Instant start = Instant.now();
        dataFolder = getDataFolder();
        plugin = this;

        registerConfigs();
        PlayerRank playerRank = new PlayerRank(config);
        database = new Database(config, playerRank);
        (muteSystem = new MuteSystem()).readMutesFromTheConfigurationFile();


        setUpResourceBundle();

        /* Create plugin manager */

        CommandsManager commandsManager = createCommandsManager();
        commandsManager.registerCommands();

        /* Those commands below are always ON (they do not depend on database) */

        /* Register listeners */

        getProxy().getPluginManager().registerListener(this, new PlayerJoin(database, messages, config));
        getProxy().getPluginManager().registerListener(this, new MuteHandler(muteSystem, messages));

        /* Register plugin channel to send sounds to bukkit server */

        getProxy().registerChannel(SoundChannel.channel);


        if (database.reloadDataSource()) {
            //If plugin has connected with database
            database.saveDefaultTables();
            //commandsManager.startDeletingThreads();
            commandsManager.registerCommands();
            getLogger().info(ChatColor.GREEN + "Launched in: " + ((float) (Instant.now().toEpochMilli() - start.toEpochMilli())) / 1000 + " s");
        } else {
            getLogger().warning(ChatColor.RED + "Plugin was not loaded!");
            getLogger().warning(ChatColor.YELLOW + "Check out your data typed in plugins/MaintainOrder/config.yml");
        }

    }

    @Override
    public void onDisable() {
        muteSystem.saveMutesToTheTemporaryFile(getDataFolder());
        database.close();
    }

    public void registerConfigs() {
        config = new Config("config.yml");
        config.saveDefaultConfig();
        config.reloadCustomConfig();
    }

    private CommandsManager createCommandsManager() {
        CommandsManager commandsManager = new CommandsManager(this);
        commandsManager.put("ban", new Ban(database, messages));
        commandsManager.put("gban", new GBan(database, messages));
        commandsManager.put("gunban", new Gunban(database, messages));
        commandsManager.put("unban", new Unban(database, messages));
        commandsManager.put("gkick", new GKick(messages));
        commandsManager.put("kick", new Kick(messages));
        commandsManager.put("mute", new Mute(muteSystem, messages));
        commandsManager.put("mutechat", new MuteChat(muteSystem, messages));
        commandsManager.put("unmute", new UnMute(muteSystem, messages));
        commandsManager.put("gungan", new Gungan(messages));
        commandsManager.put("help-mo", new HelpMO(messages, commandsManager));
        //new Info(muteSystem, database, messages),
        //commandsManager.put("ban", new ReloadMO(messages, database, null));

        return commandsManager;
    }

    private void setUpResourceBundle() {
        String language = config.get().getString("language");
        if (language == null)
            messages = new Messages();
        else
            messages = new Messages(new Locale(language));
    }
}