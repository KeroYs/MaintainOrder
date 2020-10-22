package com.github.multidestroy.commands.bans;

import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.BanCreator;
import com.github.multidestroy.commands.assets.CommandCreator;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.time.Instant;

public class GBan extends Command {

    private final Database dataBase;
    private final Messages messages;
    private final CommandCreator creator;
    private final Config config;

    public GBan(Database dataBase, Messages messages, Config config) {
        super("gban", CommandPermissions.gban);
        this.dataBase = dataBase;
        this.messages = messages;
        this.config = config;
        this.creator = new BanCreator(messages);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage = Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.GBAN.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.GBAN.HOVER_EVENT"));
        if (args.length < 3) {
            sender.sendMessage(correctUsage);
        } else {

            String reason = Utils.mergeArray(args, 2);

            if (Utils.isUnderLimit(sender, messages, args[0], reason))
                if (creator.timeCorrectness(sender, correctUsage, args[1]))
                    Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> startAsync(sender, args[1], reason, args[0]));

        }
    }

    private void startAsync(CommandSender sender, String time, String reason, String playerName) {
        ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(playerName);
        Instant now = Instant.now();
        boolean save = dataBase.saveBan("blacklist", sender, playerName, reason, now, creator.argTimeConvert(time, now));
        time = creator.translateArgTime(time);

        SpecialTypeInfo specialTypeInfo = new SpecialTypeInfo();
        {
            specialTypeInfo.setReceiver(playerName);
            specialTypeInfo.setReason(reason);
            specialTypeInfo.setTime(time);
            specialTypeInfo.setGiver(sender.getName());
        }

        if (save) {
            if (receiver != null) {
                receiver.disconnect(TextComponent.fromLegacyText(messages.getSpecialMessage(
                        SpecialType.COMMAND_GBAN_RECEIVER,
                        specialTypeInfo
                )));
            }
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(messages.getSpecialMessage(
                    SpecialType.COMMAND_GBAN_GLOBAL,
                    specialTypeInfo
                    )));
            SoundChannel.sendNetworkSound(config.get().getString("sound.gban"));
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.ERROR")));
        }
    }
}