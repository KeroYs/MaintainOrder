package commands.kick;

import com.github.multidestroy.commands.kick.Kick;
import com.github.multidestroy.i18n.Messages;
import commands.assets.MaintainOrderCommandTest;
import commands.assets.NicknameArgument;
import commands.assets.ReasonArgument;
import fake_objects.FakeProxiedPlayer;
import fake_objects.FakeServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class KickTest extends MaintainOrderCommandTest implements NicknameArgument, ReasonArgument {
    @Override
    public void setCommand() {
        command = new Kick(new Messages());
    }

    @Override
    public void simpleTest() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("Player2000", executor.getServer(), true);
        command.execute(executor, new String[]{player.getName(), "Normal", "Reason"});

        Assert.assertFalse(player.isConnected());
    }

    @Override @Test
    public void wrongNicknameLength() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("Player2000IsTooLong", executor.getServer(), true);
        command.execute(executor, new String[]{player.getName(), "Normal", "Reason"});

        Assert.assertTrue(player.isConnected());
    }

    @Override @Test
    public void wrongReasonLength() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("Player2000IsTooLong", executor.getServer(), true);

        String[] args = new String[31];
        Arrays.fill(args, 1, 31, "length: 21 * 30 > 300");
        args[0] = player.getName();


        command.execute(executor, args);
        Assert.assertTrue(player.isConnected());
    }

    @Test
    public void playerDifferentServer() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("Player2000", FakeServer.createNewServer("NewServer"), true);
        command.execute(executor, new String[]{player.getName(), "Normal", "Reason"});

        Assert.assertTrue(player.isConnected());
    }

    @Test
    public void playerOffline() {
        try {
            command.execute(executor, new String[]{"Null", "Normal", "Reason"});
        } catch (NullPointerException ex) {
            Assert.fail();
        }
    }

    @Override
    public void correctTest() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("Player2000", executor.getServer(), true);
        ProxiedPlayer player2 = FakeProxiedPlayer.createNewPlayer("2000Player", executor.getServer(), true);
        command.execute(executor, new String[]{player.getName(), "Normal", "Reason"});
        command.execute(executor, new String[]{player2.getName(), "Other", "Reason"});

        Assert.assertFalse(player.isConnected() || player2.isConnected());
    }

}
