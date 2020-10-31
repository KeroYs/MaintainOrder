package commands.mute;

import com.github.multidestroy.commands.mute.MuteChat;
import com.github.multidestroy.environment.MuteSystem;
import com.github.multidestroy.i18n.Messages;
import commands.assets.MaintainOrderCommandTest;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MuteChatTest extends MaintainOrderCommandTest {

    private MuteSystem muteSystem;

    @Before
    @Override
    public void setCommand() {
        command = new MuteChat(muteSystem = new MuteSystem(), new Messages());
    }

    @Test
    public void simpleTest() {
        command.execute(executor, new String[]{});

        Assert.assertFalse(getServerChatStatus(executor));
    }

    @Test
    public void wrongArgumentTest() {
        command.execute(executor, new String[]{"Wrong argument"});

        Assert.assertTrue(getServerChatStatus(executor));
    }

    @Test
    public void sameStatusTest() {
        command.execute(executor, new String[]{"on"});

        Assert.assertTrue(getServerChatStatus(executor));
    }

    @Override
    public void correctTest() {
        command.execute(executor, new String[]{"off"});
        command.execute(executor, new String[]{"on"});
        command.execute(executor, new String[]{"on"});
        command.execute(executor, new String[]{"off"});
        command.execute(executor, new String[]{"off"});
        command.execute(executor, new String[]{});
        command.execute(executor, new String[]{});

        Assert.assertFalse(getServerChatStatus(executor));
    }


    public boolean getServerChatStatus(ProxiedPlayer player) {
        return muteSystem.getChatStatus(player.getServer().getInfo());
    }


}


