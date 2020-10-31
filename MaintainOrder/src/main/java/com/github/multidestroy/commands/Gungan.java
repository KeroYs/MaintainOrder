package com.github.multidestroy.commands;

import com.github.multidestroy.Config;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Random;

public class Gungan extends MaintainOrderCommand {

    public Gungan(Messages messages) {
        //TODO remove null
        super(messages, "gungan", CommandPermissions.gunban, 0);
    }


    @Override
    public void start(ProxiedPlayer executor, String[] args) {
        switch (new Random().nextInt(3)) {
            case 0:
                executor.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Mesa called Jar Jar Binks, mesa your humble servant!"));
                break;
            case 1:
                executor.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "The mostest safest place would be Gunga City"));
                break;
            case 2:
                executor.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Who, mesa?"));
                break;
        }
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return new TextComponent(ChatColor.RED + "/gungan");
    }
}