package com.github.multidestroy.info;

public enum PlayerActivityStatus {
    REGISTRATION(1),
    SUCCESSFUL_LOGIN(2),
    UNSUCCESSFUL_LOGIN(3),
    PASSWORD_CHANGE(4),
    EMAIL_CHANGE(5);

    private int id;

    PlayerActivityStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}