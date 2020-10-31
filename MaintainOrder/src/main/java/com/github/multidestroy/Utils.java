package com.github.multidestroy;

import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static String arrayToString (String[] reason, int beg) {
        StringBuilder sb = new StringBuilder();
        for (int i = beg; i < reason.length; i++) {
            sb.append(reason[i]);
            if (i + 1 < reason.length)
                sb.append(' ');
        }

        return sb.toString();
    }

    public static void sendGlobalMessage(ServerInfo server, BaseComponent[] message) {
        server.getPlayers().forEach(player -> player.sendMessage(message));
    }

    public static boolean isUnderLimit(CommandSender sender, Messages messages, String nick, String reason) {
        if(nick.length() > 17) {
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.RESTRICTION.CHAR_LENGTH.NICK")));
            return false;
        }

        if(reason.length() > 300) {
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.RESTRICTION.CHAR_LENGTH.REASON")));
            return false;
        }

        return true;
    }

    public static boolean isUnderLimit(CommandSender sender, Messages messages, String nick) {
        if(nick.length() > 17) {
            sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.RESTRICTION.CHAR_LENGTH.NICK")));
            return false;
        }
        return true;
    }

    public static TextComponent createHoverEvent(String text, String hoverText) {
        TextComponent textComponent = new TextComponent(text);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hoverText).create()
        ));

        return textComponent;
    }

}