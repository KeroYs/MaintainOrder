package commands;

import com.github.multidestroy.commands.Gungan;
import com.github.multidestroy.commands.HelpMO;
import com.github.multidestroy.commands.bans.Ban;
import com.github.multidestroy.commands.bans.GBan;
import com.github.multidestroy.commands.bans.Gunban;
import com.github.multidestroy.commands.bans.Unban;
import com.github.multidestroy.commands.kick.GKick;
import com.github.multidestroy.commands.kick.Kick;
import com.github.multidestroy.commands.mute.Mute;
import com.github.multidestroy.commands.mute.MuteChat;
import com.github.multidestroy.commands.mute.UnMute;
import com.github.multidestroy.environment.CommandsManager;
import com.github.multidestroy.i18n.Messages;
import commands.assets.MaintainOrderCommandTest;
import net.md_5.bungee.api.plugin.Plugin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HelpMOTest extends MaintainOrderCommandTest {


    @Override
    public void setCommand() {
        Messages messages = new Messages();
        command = new HelpMO(messages, createCommandsManager(messages));
    }

    @Override
    public void simpleTest() {
        try {
            command.execute(executor, new String[]{});
        } catch (NullPointerException ex) {
            Assert.fail();
        }
    }

    @Override
    public void correctTest() {
        try {
            command.execute(executor, new String[]{});
            command.execute(executor, new String[]{"1"});
            command.execute(executor, new String[]{"2"});
        } catch (NullPointerException ex) {
            Assert.fail();
        }
    }

    private CommandsManager createCommandsManager(Messages messages) {
        CommandsManager commandsManager = new CommandsManager(null);
        commandsManager.put("ban", new Ban(null, messages));
        commandsManager.put("gban", new GBan(null, messages));
        commandsManager.put("gunban", new Gunban(null, messages));
        commandsManager.put("unban", new Unban(null, messages));
        commandsManager.put("gkick", new GKick(messages));
        commandsManager.put("kick", new Kick(messages));
        commandsManager.put("mute", new Mute(null, messages));
        commandsManager.put("mutechat", new MuteChat(null, messages));
        commandsManager.put("unmute", new UnMute(null, messages));
        commandsManager.put("gungan", new Gungan(messages));
        commandsManager.put("help-mo", new HelpMO(messages, commandsManager));

        return commandsManager;
    }
}
