package commands.assets;

import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import fake_objects.FakeProxiedPlayer;
import fake_objects.FakeProxyServer;
import fake_objects.FakeServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.junit.Before;
import org.junit.Test;

public abstract class MaintainOrderCommandTest {

    protected ProxiedPlayer executor;
    protected MaintainOrderCommand command;

    @Before
    public final void beforeEach() {
        //Create ProxyServer
        FakeProxyServer.getClearInstance();

        //Create default server
        Server testServer = FakeServer.createNewServer("TestServer");

        //Create default player
        executor = FakeProxiedPlayer.createNewPlayer("Admin", testServer, true);
    }

    @Before
    public abstract void setCommand();

    @Test
    public abstract void simpleTest();

    @Test
    public abstract void correctTest();

}
