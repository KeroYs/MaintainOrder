package commands.kick;

import com.github.multidestroy.commands.kick.GKick;
import com.github.multidestroy.i18n.Messages;
import fake_objects.FakeProxiedPlayer;
import fake_objects.FakeServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.junit.Assert;
import org.junit.Test;

public class GKickTest extends KickTest {

    @Override
    public void setCommand() {
        command = new GKick(new Messages());
    }

    @Override
    public void playerDifferentServer() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("Player2000", FakeServer.createNewServer("NewServer"), true);
        command.execute(executor, new String[]{player.getName(), "Normal", "Reason"});

        Assert.assertFalse(player.isConnected());
    }

    @Override
    public void playerOffline() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("Player2000", executor.getServer(), false);
        command.execute(executor, new String[]{player.getName(), "Normal", "Reason"});

        Assert.assertFalse(player.isConnected());
    }
}
