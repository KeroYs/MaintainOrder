package com.github.multidestroy;

import com.github.multidestroy.database.Database;
import com.github.multidestroy.info.BanData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.time.Instant;
import java.util.regex.Pattern;

public class Messages {

    private static class Message {
        String message;
        boolean receiver;
        boolean giver;
        boolean time;
        boolean reason;
        boolean left_time;
        boolean expiration_date;

        Message(String message, boolean receiver, boolean giver, boolean time, boolean reason, boolean left_time, boolean expiration_date) {
            this.message = message;
            this.receiver = receiver;
            this.giver = giver;
            this.time = time;
            this.reason = reason;
            this.left_time = left_time;
            this.expiration_date = expiration_date;
        }
    }

    private Message banGlobal;
    private Message banReceiver;
    private Message gBanGlobal;
    private Message gBanReceiver;
    private Message muteGlobal;
    private Message muteChatON;
    private Message muteChatOFF;
    private Message kickGlobal;
    private Message kickReceiver;
    private Message banEvent;
    private Message gBanEvent;
    private Message muteEvent;
    private BaseComponent[] ipBlockadeEvent;
    private BaseComponent[] chatOFFEvent;

    private final Config messageConfig;
    private final MuteSystem muteSystem;
    private final Database database;

    public Messages(Config messageConfig, Database database, MuteSystem muteSystem) {
        this.messageConfig = messageConfig;
        this.database = database;
        this.muteSystem = muteSystem;

        reloadFromConfig();
    }

    public void reloadFromConfig() {
        banGlobal = getMessageFromConfig("command.ban.global", true, true, true, true, false, false);
        banReceiver = getMessageFromConfig("command.ban.receiver", false, true, true, true, false, false);
        gBanGlobal = getMessageFromConfig("command.gban.global", true, true, true, true, false, false);
        gBanReceiver = getMessageFromConfig("command.gban.receiver", false, true, true, true, false, false);
        muteGlobal = getMessageFromConfig("command.mute.global", true, true, true, true, false, false);
        muteChatON = getMessageFromConfig("command.mutechat._on", false, true, false, false, false, false);
        muteChatOFF = getMessageFromConfig("command.mutechat._off", false, true, false, false, false, false);
        kickGlobal = getMessageFromConfig("command.kick/gkick.global", true, true, false, true, false, false);
        kickReceiver = getMessageFromConfig("command.kick/gkick.receiver", false, true, false, true, false, false);
        gBanEvent = getMessageFromConfig("event.gban", false, true, false, true, true, true);
        banEvent = getMessageFromConfig("event.ban", false, true, false, true, true, true);
        muteEvent = getMessageFromConfig("event.mute", false, false, false, false, true, false);
        ipBlockadeEvent = TextComponent.fromLegacyText(Utils.mergeListWithNewLines(messageConfig.get().getStringList("event.ip_blockade")));
        chatOFFEvent = TextComponent.fromLegacyText(Utils.mergeListWithNewLines(messageConfig.get().getStringList("event.chatoff")));
    }

    private Message getMessageFromConfig(String path, boolean receiver, boolean giver, boolean time, boolean reason, boolean left_time, boolean expiration_date) {
        String finalString = Utils.mergeListWithNewLines(messageConfig.get().getStringList(path));
        String finalInLowerCase = finalString.toLowerCase();

        if(receiver)
            receiver = finalInLowerCase.contains("<receiver>");
        if(giver)
            giver = finalInLowerCase.contains("<giver>");
        if(time)
            time = finalInLowerCase.contains("<time>");
        if(reason)
            reason = finalInLowerCase.contains("<reason>");
        if(left_time)
            left_time = finalInLowerCase.contains("<left_time>");
        if(expiration_date)
            expiration_date = finalInLowerCase.contains("<expiration_date>");

        return new Message(finalString, receiver, giver, time, reason, left_time, expiration_date);

    }

    private BaseComponent[] getFinalTextComponentCommand(Message message, String receiver, String giver, String reason, String time) {
        String finalMessage = message.message;

        if(message.receiver)
            finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Receiver>"), receiver);
        if(message.giver)
            finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Giver>"), giver);
        if(message.time)
            finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Time>"), time);
        if(message.reason)
            finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Reason>"), reason);

        return TextComponent.fromLegacyText(finalMessage);
    }

    private BaseComponent[] getFinalTextComponentEventBan(Message message, String serverName, String playerName) {
        String finalMessage = message.message;
        BanData ban;
        if(message.giver || message.reason || message.left_time || message.expiration_date) {
            ban = database.getLastExpiringBan(serverName, playerName);

            if (message.giver)
                finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Giver>"), (ban == null)? "ERROR" : ban.getGiverID());
            if (message.reason)
                finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Reason>"), (ban == null)? "ERROR" : ban.reason);
            if (message.left_time)
                finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Left_time>"),
                        (ban == null)? "ERROR" : (ban.isPerm())? "perm" : Utils.getLeftTimeAsString(Instant.now(), ban.getExpiration()));
            if (message.expiration_date)
                finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Expiration_date>"),
                        (ban == null)? "ERROR" : (ban.isPerm())? "perm" : Utils.getTimeAsString(ban.getExpiration()));
        }

        return TextComponent.fromLegacyText(finalMessage);
    }

    private BaseComponent[] getFinalTextComponentEventMute(Message message, ServerInfo server, String playerName, Instant now) {
        String finalMessage = message.message;

        if(message.left_time) {
            String leftTime = Utils.getLeftTimeAsString(now, muteSystem.getPlayerMuteExpiration(server, playerName));
            finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote("<Left_time>"), leftTime);
        }

        return TextComponent.fromLegacyText(finalMessage);
    }

    public BaseComponent[] getBanGlobal(String receiver, String giver, String reason, String time) {
        return getFinalTextComponentCommand(banGlobal, receiver, giver, reason, time);
    }

    public BaseComponent[] getBanReceiver(String giver, String reason, String time) {
        return getFinalTextComponentCommand(banReceiver, null, giver, reason, time);
    }

    public BaseComponent[] getGBanGlobal(String receiver, String giver, String reason, String time) {
        return getFinalTextComponentCommand(gBanGlobal, receiver, giver, reason, time);
    }

    public BaseComponent[] getGBanReceiver(String giver, String reason, String time) {
        return getFinalTextComponentCommand(gBanReceiver, null, giver, reason, time);
    }

    public BaseComponent[] getMuteGlobal(String receiver, String giver, String reason, String time) {
        return getFinalTextComponentCommand(muteGlobal, receiver, giver, reason, time);
    }

    public BaseComponent[] getMuteChatON(String giver) {
        return getFinalTextComponentCommand(muteChatON, null, giver, null, null);
    }

    public BaseComponent[] getMuteChatOFF(String giver) {
        return getFinalTextComponentCommand(muteChatOFF, null, giver, null, null);
    }

    public BaseComponent[] getKickGlobal(String receiver, String giver, String reason) {
        return getFinalTextComponentCommand(kickGlobal, receiver, giver, reason, null);
    }

    public BaseComponent[] getKickReceiver(String giver, String reason) {
        return getFinalTextComponentCommand(kickReceiver, null, giver, reason, null);
    }

    public BaseComponent[] getGBanEvent(String serverName, String playerName) {
        return getFinalTextComponentEventBan(gBanEvent, serverName, playerName);
    }

    public BaseComponent[] getBanEvent(String serverName, String playerName) {
        return getFinalTextComponentEventBan(banEvent, serverName, playerName);
    }

    public BaseComponent[] getIpBlockadeEvent() {
        return ipBlockadeEvent;
    }

    public BaseComponent[] getMuteEvent(ServerInfo server, String playerName, Instant now) {
        return getFinalTextComponentEventMute(muteEvent, server, playerName, now);
    }

    public BaseComponent[] getChatOFFEvent() {
        return chatOFFEvent;
    }
}