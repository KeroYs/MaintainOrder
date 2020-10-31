package fake_objects;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class FakeServer implements Server {

    private final ServerInfo serverInfo;

    public FakeServer(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public ServerInfo getInfo() {
        return serverInfo;
    }

    @Override
    public void sendData(String channel, byte[] data) {

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

    }

    @Override
    public void disconnect(BaseComponent... reason) {

    }

    @Override
    public void disconnect(BaseComponent reason) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Unsafe unsafe() {
        return null;
    }

    public static Server createNewServer(String name) {
        ServerInfo info = new FakeServerInfo(name);
        Server server = new FakeServer(info);
        FakeProxyServer.getInstance().addServer(info);
        return server;
    }
}
