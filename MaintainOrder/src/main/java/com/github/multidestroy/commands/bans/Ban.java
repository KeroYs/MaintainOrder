package com.github.multidestroy.commands.bans;


import com.github.multidestroy.*;
import com.github.multidestroy.commands.assets.BanCreator;
import com.github.multidestroy.commands.assets.CommandCreator;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.time.Instant;

public class Ban extends Command {

    private final Database dataBase;
    private final Messages messages;
    private final CommandCreator creator;
    private final Config config;
    private final Config notificationsConfig;

    public Ban(Database dataBase, Messages messages, Config config, Config notificationsConfig) {
        super("ban", CommandPermissions.ban);
        this.dataBase = dataBase;
        this.messages = messages;
        this.config = config;
        this.notificationsConfig = notificationsConfig;
        this.creator = new BanCreator(notificationsConfig);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ServerInfo server;
        ProxiedPlayer receiver;
        String receiverName;
        String time;
        String reason;
        TextComponent correctUsage;

        if(sender instanceof ProxiedPlayer) {
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.ban.hover_event", "commands.ban.game_correct_usage");
            //Player
            if (args.length < 3) {
                sender.sendMessage( correctUsage );
                return;
            }
            else {
                server = ((ProxiedPlayer) sender).getServer().getInfo();
                receiver = ProxyServer.getInstance().getPlayer(args[0]);
                reason = Utils.mergeArray(args, 2);
                time = args[1];
                receiverName = args[0];
            }
        } else {
            correctUsage =
                    Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.ban.hover_event", "commands.ban.console_correct_usage");
            //Console
            if (args.length < 4) {
                sender.sendMessage(correctUsage);
                return;
            }
            else {
                server = ProxyServer.getInstance().getServerInfo(args[0]);
                if(server == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.wrong_server_name")));
                    return;
                }
                receiver = ProxyServer.getInstance().getPlayer(args[1]);
                reason = Utils.mergeArray(args, 3);
                time = args[2];
                receiverName = args[1];
            }
        }

        if(!Utils.isUnderLimit(sender, notificationsConfig, receiverName, reason))
            return;

        if(creator.timeCorrectness(sender, correctUsage, time))
            Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> startAsync(sender, receiver, receiverName, server, time, reason));
    }

    private void startAsync(CommandSender sender, ProxiedPlayer receiver, String receiverName, ServerInfo server, String time, String reason) {
        boolean save;

        Instant now = Instant.now();
        save = dataBase.saveBan(server.getName(), sender, receiverName, reason, now, creator.argTimeConvert(time, now));
        time = creator.translateArgTime(time);

        if (save) {
            if (receiver != null && receiver.getServer().getInfo().getName().equals(server.getName()))
                receiver.disconnect(messages.getBanReceiver(sender.getName(), reason, time));
            Utils.sendGlobalMessage(server, messages.getBanGlobal(receiverName, sender.getName(), reason, time));
            SoundChannel.sendServerSound(server, config.get().getString("sound.ban"));
        } else
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("database.error")));
    }
}