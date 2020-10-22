package com.github.multidestroy.commands.assets;

import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class CommandCreator {

    protected Messages messages;

    public CommandCreator(Messages messages) {
        this.messages = messages;
    }

    public abstract void sendCorrectTimeValues(CommandSender sender, char type);

    protected abstract int argTimeValuesCorrectness(int value, char type);

    public abstract boolean timeCorrectness(CommandSender sender, TextComponent correctUsage, String time);

    protected boolean timeCorrectnessNotPerm(CommandSender sender, TextComponent correctUsage, String time) {
        switch (argTimeCorrectness(time)) {
            case -1:
                sender.sendMessage(correctUsage);
                return false;
            case 0:
                sendCorrectTimeValues(sender, time.charAt(time.length() - 1));
                return false;
        }
        return true;
    }

    protected int argTimeCorrectness(String time) {
        int i = 0; //index of last digit + 1
        while (i != time.length() && time.charAt(i) >= '0' && time.charAt(i) <= '9')
            i++;

        if (i == time.length() || i + 1 != time.length())
            return -1;

        //number of time
        int value = Integer.parseInt(time.substring(0, i));

        return argTimeValuesCorrectness(value, time.charAt(i));
    }

    public Instant argTimeConvert(String time, Instant now) {
        if (time == null)
            return null;

        if(time.equalsIgnoreCase("perm"))
            return Instant.ofEpochMilli(Long.MAX_VALUE);

        char type = time.charAt(time.length() - 1);
        long value = Long.parseLong(time.substring(0, time.length() - 1));

        if (type == 'm')
            return now.plusSeconds(TimeUnit.SECONDS.convert(value, TimeUnit.MINUTES));
        if (type == 'h')
            return now.plusSeconds(TimeUnit.SECONDS.convert(value, TimeUnit.HOURS));
        if (type == 'd')
            return now.plusSeconds(TimeUnit.SECONDS.convert(value, TimeUnit.DAYS));

        return null;
    }

    public String translateArgTime(String time) {
        if(time.equalsIgnoreCase("perm"))
            return time;

        int size = time.length();
        char type = time.toLowerCase().charAt(size - 1);
        int value = Integer.parseInt(time.substring(0, size - 1));

        StringBuilder result = new StringBuilder().append(value);
        switch (type) {
            case 'm':
                result.append("m");
                break;
            case 'h':
                result.append("h");
                break;
            case 'd':
                result.append("d");
                break;
        }

        return result.toString();
    }

}