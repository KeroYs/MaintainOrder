package fake_objects;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class FakeProxyServer extends ProxyServer {

    private final Map<String, ProxiedPlayer> players;
    private final Map<String, ServerInfo> servers;
    private final PluginManager pluginManager;

    public FakeProxyServer() {
        players = new HashMap<>();
        servers = new HashMap<>();
        pluginManager = new PluginManager(this);
    }

    public void addPlayer(ProxiedPlayer... proxiedPlayers) {
        for (ProxiedPlayer player : proxiedPlayers) {
            players.put(player.getName(), player);
        }
    }

    public void addServer(ServerInfo... serverInfo) {
        for (ServerInfo info : serverInfo) {
            servers.put(info.getName(), info);
        }
    }

    public void reset() {
        players.clear();
        servers.clear();
    }

    @Override
    public String getName() {
        return "FakeProxyServer";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getTranslation(String name, Object... args) {
        return "Not supported";
    }

    @Override
    public Logger getLogger() {
        return Logger.getGlobal(); //TODO is this working?
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        return players.values();
    }

    @Override
    public ProxiedPlayer getPlayer(String name) {
        return players.get(name);
    }

    @Override
    public ProxiedPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        return servers;
    }

    @Override
    public ServerInfo getServerInfo(String name) {
        return servers.get(name);
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public ConfigurationAdapter getConfigurationAdapter() {
        //Not supported
        return null;
    }

    @Override
    public void setConfigurationAdapter(ConfigurationAdapter adapter) {
        //Not supported
    }

    @Override
    public ReconnectHandler getReconnectHandler() {
        //Not supported
        return null;
    }

    @Override
    public void setReconnectHandler(ReconnectHandler handler) {
        //Not supported
    }

    @Override
    public void stop() {
        //Not supported
    }

    @Override
    public void stop(String reason) {
        //Not supported
    }

    @Override
    public void registerChannel(String channel) {
        //Not supported
    }

    @Override
    public void unregisterChannel(String channel) {
        //Not supported
    }

    @Override
    public Collection<String> getChannels() {
        return null;
    }

    @Override
    public String getGameVersion() {
        return null;
    }

    @Override
    public int getProtocolVersion() {
        return 0;
    }

    @Override
    public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
        return null;
    }

    @Override
    public ServerInfo constructServerInfo(String name, SocketAddress address, String motd, boolean restricted) {
        return null;
    }

    @Override
    public CommandSender getConsole() {
        return null;
    }

    @Override
    public File getPluginsFolder() {
        return null;
    }

    @Override
    public TaskScheduler getScheduler() {
        return null;
    }

    @Override
    public int getOnlineCount() {
        return 0;
    }

    @Override
    public void broadcast(String message) {

    }

    @Override
    public void broadcast(BaseComponent... message) {

    }

    @Override
    public void broadcast(BaseComponent message) {

    }

    @Override
    public Collection<String> getDisabledCommands() {
        return null;
    }

    @Override
    public ProxyConfig getConfig() {
        return null;
    }

    @Override
    public Collection<ProxiedPlayer> matchPlayer(String match) {
        return null;
    }

    @Override
    public Title createTitle() {
        return null;
    }

    public static FakeProxyServer getClearInstance() {
        FakeProxyServer fakeProxyServer;

        if (ProxyServer.getInstance() == null) {
            fakeProxyServer = new FakeProxyServer();
            ProxyServer.setInstance(fakeProxyServer);
        } else {
            fakeProxyServer = (FakeProxyServer) ProxyServer.getInstance();
            fakeProxyServer.reset();
        }
        return fakeProxyServer;
    }

    public static FakeProxyServer getInstance() {
        FakeProxyServer fakeProxyServer;

        if (ProxyServer.getInstance() == null) {
            fakeProxyServer = new FakeProxyServer();
            ProxyServer.setInstance(fakeProxyServer);
        } else {
            fakeProxyServer = (FakeProxyServer) ProxyServer.getInstance();
        }
        return fakeProxyServer;
    }
}
