package com.github.multidestroy.info;

import com.github.multidestroy.Config;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;

public class PlayerRank {

    private Config config;

    public PlayerRank(Config config) {
        this.config = config;
    }

    public BaseComponent[] getRank(int id) {
        String rank = config.get().getSection("database.ranks.id").getString(Integer.toString(id));
        if(rank != null && rank.length() == 0)
            return TextComponent.fromLegacyText(rank);
        return TextComponent.fromLegacyText(config.get().getString("database.ranks.id.not_exists"));
    }

    public BaseComponent[] getNotExistRank() {
        return TextComponent.fromLegacyText(config.get().getString("database.ranks.id.not_exists"));
    }
}
