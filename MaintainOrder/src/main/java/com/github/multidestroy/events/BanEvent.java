package com.github.multidestroy.events;

import net.md_5.bungee.api.plugin.Event;

public class BanEvent extends Event implements PunishmentEvent {

    private boolean global;
    private boolean console;

    public BanEvent(boolean global, boolean console) {
        this.global = global;
        this.console = console;
    }

}
