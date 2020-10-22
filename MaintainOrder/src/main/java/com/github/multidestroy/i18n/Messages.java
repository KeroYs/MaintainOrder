package com.github.multidestroy.i18n;

import net.md_5.bungee.api.ChatColor;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages extends ResourceBundle {

    public Messages() {
        parent = ResourceBundle.getBundle("MoMessages", new UTF8Control());
    }

    public Messages(Locale locale) {
        parent = ResourceBundle.getBundle("MoMessages", locale, new UTF8Control());
    }

    public String getSpecialMessage(SpecialType specialType, SpecialTypeInfo specialTypeInfo) {
        return ChatColor.translateAlternateColorCodes('&', specialType.getString(parent, specialTypeInfo));
    }

    @Override
    protected Object handleGetObject(String key) {
        Object value = parent.getObject(key);

        if (value instanceof String)
            return ChatColor.translateAlternateColorCodes('&', (String) value);
        return parent.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return parent.getKeys();
    }

}