package commands.mute;

import com.github.multidestroy.commands.mute.UnMute;
import com.github.multidestroy.environment.MuteSystem;
import com.github.multidestroy.i18n.Messages;
import commands.assets.MaintainOrderCommandTest;
import commands.assets.NicknameArgument;
import fake_objects.FakeProxiedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UnMuteTest extends MaintainOrderCommandTest implements NicknameArgument {

    private MuteSystem muteSystem;

    @Before
    @Override
    public void setCommand() {
        command = new UnMute(muteSystem = new MuteSystem(), new Messages());
    }

    @Override
    public void simpleTest() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("MutedPlayer", executor.getServer(), true);
        mutePlayer(player);

        command.execute(executor, new String[]{player.getName()});
        Assert.assertFalse(isPlayerMuted(player));
    }

    @Override @Test
    public void wrongNicknameLength() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("MutedPlayerButThisNameIsWayTooLong", executor.getServer(), true);
        mutePlayer(player);

        command.execute(executor, new String[]{player.getName()});
        Assert.assertTrue(isPlayerMuted(player));
    }

    @Test
    public void playerOffline() {
        try {
            command.execute(executor, new String[]{"NullPlayer"});
        } catch (NullPointerException ex) {
            Assert.fail();
        }
    }

    @Test
    public void playerNotMuted() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("MutedPlayer", executor.getServer(), true);

        command.execute(executor, new String[]{player.getName()});
        Assert.assertFalse(isPlayerMuted(player));
    }

    @Override
    public void correctTest() {
        ProxiedPlayer player = FakeProxiedPlayer.createNewPlayer("MutedPlayer", executor.getServer(), true);
        ProxiedPlayer player2 = FakeProxiedPlayer.createNewPlayer("FakePlayer", executor.getServer(), true);
        mutePlayer(player);
        mutePlayer(player2);

        command.execute(executor, new String[]{player.getName()});
        command.execute(executor, new String[]{player2.getName()});

        Assert.assertFalse(isPlayerMuted(player) && isPlayerMuted(player2));
    }

    private void mutePlayer(ProxiedPlayer player) {
        muteSystem.givePlayerMute(player.getServer().getInfo(), player.getName(), Instant.now().plus(10, ChronoUnit.MINUTES));
    }

    private boolean isPlayerMuted(ProxiedPlayer player) {
        return muteSystem.isPlayerMuted(player.getServer().getInfo(), player.getName());
    }

}
