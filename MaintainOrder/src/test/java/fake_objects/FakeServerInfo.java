package fake_objects;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;

public class FakeServerInfo implements ServerInfo {

    private String name;
    private HashMap<String, ProxiedPlayer> players;

    public FakeServerInfo(String name) {
        this.name = name;
        players = new HashMap<>();
    }

    public void putPlayer(ProxiedPlayer player) {
        players.put(player.getName(), player);
    }

    @Override
    public String getName() {
        return name;
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
    public Collection<ProxiedPlayer> getPlayers() {
        return players.values();
    }

    @Override
    public String getMotd() {
        return null;
    }

    @Override
    public boolean isRestricted() {
        return false;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean canAccess(CommandSender sender) {
        return false;
    }

    @Override
    public void sendData(String channel, byte[] data) {

    }

    @Override
    public boolean sendData(String channel, byte[] data, boolean queue) {
        return false;
    }

    @Override
    public void ping(Callback<ServerPing> callback) {

    }
}
