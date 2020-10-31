package commands.mute;

import com.github.multidestroy.commands.mute.Mute;
import com.github.multidestroy.environment.MuteSystem;
import com.github.multidestroy.i18n.Messages;
import commands.assets.MaintainOrderCommandTest;
import commands.assets.NicknameArgument;
import commands.assets.ReasonArgument;
import commands.assets.TimeArgument;
import fake_objects.FakeProxiedPlayer;
import fake_objects.FakeServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class MuteTest extends MaintainOrderCommandTest implements NicknameArgument, TimeArgument, ReasonArgument {

    private MuteSystem muteSystem;

    @Before
    @Override
    public void setCommand() {
        command = new Mute(muteSystem = new MuteSystem(), new Messages());
    }

    @Override
    public void simpleTest() {
        ProxiedPlayer recipient = FakeProxiedPlayer.createNewPlayer("Recipient", executor.getServer(), true);

        command.execute(executor, new String[]{recipient.getName(), "10m", "This", "is", "reason!"});
        Assert.assertTrue(isPlayerMuted(recipient));
    }

    @Override @Test
    public void wrongNicknameLength() {
        ProxiedPlayer recipient = FakeProxiedPlayer.createNewPlayer("ThisNameIsWayTooLong", executor.getServer(), true);

        command.execute(executor, new String[]{recipient.getName(), "10m", "Reason"});
        Assert.assertFalse(isPlayerMuted(recipient));
    }

    @Override @Test
    public void wrongReasonLength() {
        ProxiedPlayer recipient = FakeProxiedPlayer.createNewPlayer("NormalPlayer", executor.getServer(), true);

        String[] args = new String[32];
        Arrays.fill(args, 2, 32, "length: 21 * 30 > 300");
        args[0] = recipient.getName();
        args[1] = "1m";


        command.execute(executor, args); //Wrong structure of time

        Assert.assertFalse(isPlayerMuted(recipient));
    }

    @Override @Test
    public void wrongTimeArgument() {
        ProxiedPlayer recipient = FakeProxiedPlayer.createNewPlayer("NormalPlayer", executor.getServer(), true);

        command.execute(executor, new String[]{recipient.getName(), "Wrong time", "Reason"}); //Wrong structure of time

        command.execute(executor, new String[]{recipient.getName(), "0m", "Reason"}); //Below lower compartment of minutes
        command.execute(executor, new String[]{recipient.getName(), "61m", "Reason"}); //Above upper compartment of minutes

        command.execute(executor, new String[]{recipient.getName(), "0h", "Reason"}); //Below lower compartment of hours
        command.execute(executor, new String[]{recipient.getName(), "25h", "Reason"}); //Above upper compartment of hours

        command.execute(executor, new String[]{recipient.getName(), "0d", "Reason"}); //Below lower compartment of days
        command.execute(executor, new String[]{recipient.getName(), "1d", "Reason"}); //Mute command does not accepts days as time argument
        command.execute(executor, new String[]{recipient.getName(), "365d", "Reason"}); //Above upper compartment of hours

        Assert.assertFalse(isPlayerMuted(recipient));
    }

    @Test
    public void offlinePlayerTest() {
        try {
            command.execute(executor, new String[]{"NullPlayer", "1h", "Reason", "this", "is"});
        } catch (NullPointerException ex) {
            Assert.fail();
        }
    }

    @Test
    public void differentServerTest() {
        Server server = FakeServer.createNewServer("OtherServer");
        ProxiedPlayer recipient = FakeProxiedPlayer.createNewPlayer("NormalPlayer", server, true);

        command = new Mute(muteSystem = new MuteSystem(), new Messages());

        command.execute(executor, new String[]{recipient.getName(), "1h", "Reason"}); //Wrong structure of time

        Assert.assertFalse(isPlayerMuted(recipient));
    }

    @Override
    public void correctTest() {
        ProxiedPlayer recipient = FakeProxiedPlayer.createNewPlayer("NormalPlayer", executor.getServer(), true);

        command.execute(executor, new String[]{recipient.getName(), "1h", "Reason"}); //Wrong structure of time

        Assert.assertTrue(isPlayerMuted(recipient));
    }

    public boolean isPlayerMuted(ProxiedPlayer player) {
        return (muteSystem.isPlayerMuted(player.getServer().getInfo(), player.getName()));
    }


}
