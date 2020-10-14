package com.github.multidestroy.commands;

import com.github.multidestroy.Config;
import com.github.multidestroy.commands.assets.CommandPermissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Random;

public class Gungan extends Command {
    private final Config config;

    public Gungan(Config config) {
        super("gungan", CommandPermissions.gunban);
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (new Random().nextInt(3)) {
            case 0:
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Mesa called Jar Jar Binks, mesa your humble servant!"));
                break;
            case 1:
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "The mostest safest place would be Gunga City"));
                break;
            case 2:
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Who, mesa?"));
                break;
        }
    }
}