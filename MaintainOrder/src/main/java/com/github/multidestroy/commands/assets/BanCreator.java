package com.github.multidestroy.commands.assets;

import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class BanCreator extends CommandCreator {

    public BanCreator(Messages messages) {
        super(messages);
    }

    @Override
    public boolean timeCorrectness(CommandSender sender, TextComponent correctUsage, String time) {
        if(time.equalsIgnoreCase("perm"))
            return true;
        return timeCorrectnessNotPerm(sender, correctUsage, time);
    }

    @Override
    public void sendCorrectTimeValues(CommandSender sender, char type) {
        switch (type) {
            case 'h':
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.RESTRICTION.TIME_RANGE.HOURS")));
                break;
            case 'd':
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.RESTRICTION.TIME_RANGE.DAYS")));
                break;
        }
    }

    @Override
    protected int argTimeValuesCorrectness(int value, char type) {
        //hours
        if (type == 'h')
            if (value > 24 || value < 1)
                return 0;
            else return 1;

        //days
        if (type == 'd')
            if (value > 365 || value < 1)
                return 0;
            else return 1;

        return -1;
    }
}