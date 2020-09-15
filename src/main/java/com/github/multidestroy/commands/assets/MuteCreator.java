package com.github.multidestroy.commands.assets;

import com.github.multidestroy.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class MuteCreator extends CommandCreator {

    public MuteCreator(Config notificationsConfig) {
        super(notificationsConfig);
    }

    @Override
    public boolean timeCorrectness(CommandSender sender, TextComponent correctUsage, String time) {
        return timeCorrectnessNotPerm(sender, correctUsage, time);
    }

    @Override
    public void sendCorrectTimeValues(CommandSender sender, char type) {
        switch (type) {
            case 'm':
                sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("restrictions.time_range.minutes")));
                break;
            case 'h':
                sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("restrictions.time_range.hours")));
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
