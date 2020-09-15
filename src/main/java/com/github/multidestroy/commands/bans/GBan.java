package com.github.multidestroy.commands.bans;

import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.BanCreator;
import com.github.multidestroy.commands.assets.CommandCreator;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.time.Instant;

public class GBan extends Command {

    private final Database dataBase;
    private final Messages messages;
    private final CommandCreator creator;
    private final Config config;
    private final Config notificationsConfig;

    public GBan(Database dataBase, Messages messages, Config config, Config notificationsConfig) {
        super("gban", CommandPermissions.gban);
        this.dataBase = dataBase;
        this.messages = messages;
        this.config = config;
        this.notificationsConfig = notificationsConfig;
        this.creator = new BanCreator(notificationsConfig);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage =
                Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.gban.hover_event", "commands.gban.correct_usage");
        if (args.length < 3) {
            sender.sendMessage(correctUsage);
        } else {

            String reason = Utils.mergeArray(args, 2);

            if (Utils.isUnderLimit(sender, notificationsConfig, args[0], reason))
                if (creator.timeCorrectness(sender, correctUsage, args[1]))
                    Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> startAsync(sender, args[1], reason, args[0]));

        }
    }

    private void startAsync(CommandSender sender, String time, String reason, String playerName) {
        ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(playerName);
        Instant now = Instant.now();
        boolean save = dataBase.saveBan("blacklist", sender, playerName, reason, now, creator.argTimeConvert(time, now));
        time = creator.translateArgTime(time);


        if (save) {
            if (receiver != null)
                receiver.disconnect(messages.getGBanReceiver(sender.getName(), reason, time));
            ProxyServer.getInstance().broadcast(messages.getGBanGlobal(playerName, sender.getName(), reason, time));
            SoundChannel.sendNetworkSound(config.get().getString("sound.gban"));
        } else
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("database.error")));
    }
}