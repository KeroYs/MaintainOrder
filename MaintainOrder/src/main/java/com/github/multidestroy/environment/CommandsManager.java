package com.github.multidestroy.environment;

import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;

public class CommandsManager extends HashMap<String, MaintainOrderCommand> {

    private final Plugin plugin;

    public CommandsManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        values().forEach(command -> ProxyServer.getInstance().getPluginManager().registerCommand(plugin, command));
    }

    public void unregisterCommands() {
        values().forEach(command -> ProxyServer.getInstance().getPluginManager().unregisterCommand(command));
    }

}