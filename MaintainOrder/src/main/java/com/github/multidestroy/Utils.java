package com.github.multidestroy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.protocol.packet.Chat;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String getTimeAsString(Instant time) {
        if(time != null) {

            if(time.toEpochMilli() == Long.MAX_VALUE)
                return "perm";

            String timeString = LocalDateTime.ofInstant(time, ZoneId.systemDefault()).toString();
            return timeString.replace('T', ' ').substring(0, timeString.indexOf('.'));
        }
        return null;
    }

    public static String getLeftTimeAsString(Instant now, Instant expiration) {
        if(now.isBefore(expiration)) {

            StringBuilder left = new StringBuilder();
            long leftTime = Duration.between(now, expiration).getSeconds();
            //days
            long limit = TimeUnit.SECONDS.convert(1, TimeUnit.DAYS);
            if(leftTime >= limit) {
                long days = leftTime/limit;
                leftTime -= days * limit;

                left.append(days).append("d ");
            }

            //hours
            limit = TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);
            if(leftTime >= limit) {
                long hours = leftTime/limit;
                leftTime -= hours * limit;

                left.append(hours).append("h ");
            }

            //minutes
            limit = TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);
            if(leftTime >= limit) {
                long minutes = leftTime/limit;
                leftTime -= minutes * limit;

                left.append(minutes).append("m ");
            }

            //seconds
            limit = 1;
            if(leftTime >= limit) {
                long seconds = leftTime/limit;

                left.append(seconds).append("s");
            }
            if(left.length() != 0) {
                if (left.charAt(left.length() - 1) == ' ')
                    left.delete(left.length() - 1, left.length());
            } else
                left.append("0s");
            return left.toString();
        } else return "0s";
    }

    public static String mergeArray(String[] args, int startIndex) {
        int i = 0;
        StringBuilder reason = new StringBuilder();
        for (String next : args) {
            if (i < startIndex)
                i++;
            else {
                reason.append(next).append(" ");
            }
        }
        return reason.toString();
    }

    public static String mergeListWithNewLines(List<String> list) {
        StringBuilder newString = new StringBuilder();
        int i = 0;
        for(String line : list) {
            newString.append(line);

            if(list.size() - 1 != i++)
                newString.append('\n');
        }
        return ChatColor.translateAlternateColorCodes('&', newString.toString());
    }

    public static void sendGlobalMessage(ServerInfo server, BaseComponent[] message) {
        server.getPlayers().forEach(player -> player.sendMessage(message));
    }

    public static boolean isUnderLimit(CommandSender sender, Config notificationsConfig, String nick, String reason) {
        if(nick.length() > 17) {
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("restrictions.char_length.nick")));
            return false;
        }

        if(reason.length() > 300) {
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("restrictions.char_length.reason")));
            return false;
        }

        return true;
    }

    public static boolean isUnderLimit(CommandSender sender, Config notificationsConfig, String nick) {
        if(nick.length() > 17) {
            sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("restrictions.char_length.nick")));
            return false;
        }
        return true;
    }

    public static TextComponent createHoverEvent_OneDesc(Config notificationsConfig, String hoverEventPath, String correctUsagePath) {
        TextComponent textComponent = new TextComponent(notificationsConfig.get().getString(correctUsagePath));
        Configuration config = notificationsConfig.get().getSection(hoverEventPath);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                config.getString("description") + "\n" + config.getString("example")
        ).create()));

        return textComponent;
    }

    public static TextComponent createHoverEvent_TwoDesc(Config notificationsConfig, String hoverEventPath, String correctUsagePath) {
        TextComponent textComponent = new TextComponent(notificationsConfig.get().getString(correctUsagePath));
        Configuration config = notificationsConfig.get().getSection(hoverEventPath);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                config.getSection("description").getString("low_rank") + "\n" + config.getSection("description").getString("big_rank")
                        + "\n" + config.getString("example")
        ).create()));

        return textComponent;
    }

}