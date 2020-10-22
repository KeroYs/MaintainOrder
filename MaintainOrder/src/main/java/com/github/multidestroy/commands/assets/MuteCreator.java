package com.github.multidestroy.commands.assets;

import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class MuteCreator extends CommandCreator {

    public MuteCreator(Messages messages) {
        super(messages);
    }

    @Override
    public boolean timeCorrectness(CommandSender sender, TextComponent correctUsage, String time) {
        return timeCorrectnessNotPerm(sender, correctUsage, time);
    }

    @Override
    public void sendCorrectTimeValues(CommandSender sender, char type) {
        switch (type) {
            case 'm':
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.RESTRICTION.TIME_RANGE.MINUTES")));
                break;
            case 'h':
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.RESTRICTION.TIME_RANGE.HOURS")));
                break;
        }
    }

    @Override
    protected int argTimeValuesCorrectness(int value, char type) {
        //minutes
        if (type == 'm')
            if (value > 60 || value < 1)
                return 0;
            else return 1;

        //hours
        if (type == 'h')
            if (value > 24 || value < 1)
                return 0;
            else return 1;

        return -1;
    }
}