package com.github.multidestroy.commands;

import com.github.multidestroy.Config;
import com.github.multidestroy.Main;
import com.github.multidestroy.MuteSystem;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.database.Database;
import com.github.multidestroy.info.BanData;
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
import java.util.List;

public class Info extends Command {

    enum InfoType {
        NON_SPECIFIED,
        NORMAL,
        MUTES,
        BANS,
        ALL;

        public static InfoType getType(String arg) {
            arg = arg.toLowerCase();
            if(arg.equals("mute") || arg.equals("mutes"))
                return MUTES;
            if(arg.equals("ban") || arg.equals("bans"))
                return BANS;
            if(arg.equals("all"))
                return ALL;
            return NON_SPECIFIED;
        }
    }

    private final MuteSystem muteSystem;
    private final Database dataBase;
    private final Config notificationsConfig;

    public Info(MuteSystem muteSystem, Database dataBase, Config notificationsConfig) {
        super("info", CommandPermissions.info);
        this.muteSystem = muteSystem;
        this.dataBase = dataBase;
        this.notificationsConfig = notificationsConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        InfoType infoType;
        TextComponent correctUsage =
                Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.info.hover_event", "commands.info.correct_usage");

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(correctUsage);
            return;
        } else if (args.length == 1) {
            infoType = InfoType.NORMAL;
        } else {
            infoType = InfoType.getType(args[1]);
            if (infoType == InfoType.NON_SPECIFIED) {
                sender.sendMessage(correctUsage);
                return;
            }
        }

        if (Utils.isUnderLimit(sender, notificationsConfig, args[0]))
            Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> sender.sendMessage(startAsync(args[0], infoType)));
    }

    private BaseComponent[] startAsync(String playerName, InfoType infoType) {
        ComponentBuilder cb = new ComponentBuilder();
        cb.append(new TextComponent(ChatColor.DARK_GRAY + "-------- " + ChatColor.AQUA + "Info" + ChatColor.DARK_GRAY + " --------\n"));
        cb.append(standardInfo(playerName));
        if(infoType == InfoType.MUTES || infoType == InfoType.ALL) {
            cb.append(new TextComponent(ChatColor.DARK_GRAY + "-------- " + ChatColor.AQUA + "Mutes" + ChatColor.DARK_GRAY + " -------\n"));
            BaseComponent[] mutesMessage = getMuteInfo(playerName);
            if(mutesMessage.length != 0)
                cb.append(mutesMessage);
        }
        if(infoType == InfoType.BANS || infoType == InfoType.ALL) {
            cb.append(new TextComponent(ChatColor.DARK_GRAY + "-------- " + ChatColor.AQUA + "Bans" + ChatColor.DARK_GRAY + " --------\n"));
            BaseComponent[] bansMessage = getBansInfo(playerName);
            if(bansMessage.length != 0)
                cb.append(bansMessage);
        }
        cb.append((ChatColor.DARK_GRAY + "---------------------"));
        return cb.create();
    }

    private BaseComponent[] standardInfo(String playerName) {
        ComponentBuilder cb = new ComponentBuilder();
        cb.append(ChatColor.GRAY + "Nick: " + ChatColor.RED + playerName + "\n");
        cb.append(ChatColor.GRAY + "IP address: " + ChatColor.RED + dataBase.getIpOfLastSuccessfulLogin(playerName) + "\n");
        BaseComponent[] rank = dataBase.getPlayerRank(playerName);

        cb.append(ChatColor.GRAY + "Rank: " + rank + "\n");
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        cb.append(ChatColor.GRAY + "Status: " + ChatColor.RED +(player == null ? ChatColor.DARK_RED + "Offline" : ChatColor.GREEN + "Online " + ChatColor.GRAY + "<" + player.getServer().getInfo().getName() + ">") + "\n");
        return cb.create();
    }

    private BaseComponent[] getMuteInfo(String playerName) {
        ComponentBuilder cb = new ComponentBuilder();
        ProxyServer.getInstance().getServers().values().forEach((server) -> {
            Instant expiration;
            if((expiration = muteSystem.getPlayerMuteExpiration(server, playerName)) != null)
                cb.append(ChatColor.GRAY + "<" + server.getName() + ">" + " expiration: " + ChatColor.RED + Utils.getTimeAsString(expiration) + "\n");
        });
        return cb.create();
    }

    private BaseComponent[] getBansInfo(String playerName) {
        ComponentBuilder cb1 = new ComponentBuilder();
        dataBase.getOngoingBans("blacklist", playerName).forEach(ban -> {
            cb1.append(ongoingBanInfo("blacklist", ban)).append("\n");
        });

        ProxyServer.getInstance().getServers().values().forEach(server -> {
            List<BanData> ongoingBans = dataBase.getOngoingBans(server.getName(), playerName);

            ongoingBans.forEach(ban -> {
                cb1.append(ongoingBanInfo(server.getName(), ban)).append("\n", ComponentBuilder.FormatRetention.NONE);
            });
        });
        return cb1.create();
    }

    private BaseComponent[] ongoingBanInfo(String serverName, BanData banData) {

        TextComponent begin = new TextComponent(ChatColor.GREEN + "(begin)");
        TextComponent expiration = new TextComponent(ChatColor.GREEN + "(expiration)");
        TextComponent reason = new TextComponent("(reason)");
        TextComponent giver = new TextComponent("(giver)");
        TextComponent space = new TextComponent(" - ");

        begin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.getTimeAsString(banData.getTime())).create()));
        expiration.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.getTimeAsString(banData.getExpiration())).create()));
        reason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(banData.reason).create()));
        giver.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(banData.getGiverID()).create()));
        space.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, null));


        ComponentBuilder cb = new ComponentBuilder();

        cb.append(ChatColor.GRAY + "<" + serverName + "> ").color(ChatColor.GREEN).append(begin).append(space)
                .append(expiration).append(space)
                .append(reason).append(space)
                .append(giver);

        TextComponent empty = new TextComponent("");
        empty.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, null));

        cb.append(empty);
        return cb.create();
    }

}
