package fake_objects;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FakeProxiedPlayer implements ProxiedPlayer {

    private String name;
    private Server server;
    private boolean isOnline;

    public FakeProxiedPlayer(String name, Server server, boolean isOnline) {
        this.name = name;
        this.server = server;
        this.isOnline = isOnline;
    }

    public void changeConnectionStatus(boolean status) {
        this.isOnline = status;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {

        return "Not supported";
    }

    @Override
    public void setDisplayName(String name) {
        //Not supported
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent... message) {
        //Not supported
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent message) {
        //Not supported
    }

    @Override
    public void connect(ServerInfo target) {
        //Not supported
    }

    @Override
    public void connect(ServerInfo target, ServerConnectEvent.Reason reason) {
        //Not supported
    }

    @Override
    public void connect(ServerInfo target, Callback<Boolean> callback) {
        //Not supported
    }

    @Override
    public void connect(ServerInfo target, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        //Not supported
    }

    @Override
    public void connect(ServerConnectRequest request) {
        //Not supported
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void sendData(String channel, byte[] data) {

    }

    @Override
    public PendingConnection getPendingConnection() {
        return null;
    }

    @Override
    public void chat(String message) {

    }

    @Override
    public ServerInfo getReconnectServer() {
        return null;
    }

    @Override
    public void setReconnectServer(ServerInfo server) {

    }

    @Override
    public String getUUID() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public byte getViewDistance() {
        return 0;
    }

    @Override
    public ChatMode getChatMode() {
        return null;
    }

    @Override
    public boolean hasChatColors() {
        return false;
    }

    @Override
    public SkinConfiguration getSkinParts() {
        return null;
    }

    @Override
    public MainHand getMainHand() {
        return null;
    }

    @Override
    public void setTabHeader(BaseComponent header, BaseComponent footer) {

    }

    @Override
    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {

    }

    @Override
    public void resetTabHeader() {

    }

    @Override
    public void sendTitle(Title title) {

    }

    @Override
    public boolean isForgeUser() {
        return false;
    }

    @Override
    public Map<String, String> getModList() {
        return null;
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessages(String... messages) {
    }

    @Override
    public void sendMessage(BaseComponent... message) {
    }

    @Override
    public void sendMessage(BaseComponent message) {
    }

    @Override
    public Collection<String> getGroups() {
        return null;
    }

    @Override
    public void addGroups(String... groups) {

    }

    @Override
    public void removeGroups(String... groups) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public void setPermission(String permission, boolean value) {

    }

    @Override
    public Collection<String> getPermissions() {
        return null;
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public SocketAddress getSocketAddress() {
        return null;
    }

    @Override
    public void disconnect(String reason) {
        isOnline = false;
    }

    @Override
    public void disconnect(BaseComponent... reason) {
        isOnline = false;
    }

    @Override
    public void disconnect(BaseComponent reason) {
        isOnline = false;
    }

    @Override
    public boolean isConnected() {
        return isOnline;
    }

    @Override
    public Unsafe unsafe() {
        return null;
    }

    public static ProxiedPlayer createNewPlayer(String name, Server server, boolean connection) {
        ProxiedPlayer player;
        FakeProxyServer.getInstance().addPlayer(player = new FakeProxiedPlayer(name, server, connection));
        ((FakeServerInfo) server.getInfo()).putPlayer(player);
        return player;
    }
}
