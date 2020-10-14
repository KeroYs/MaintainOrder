package com.github.multidestroy.info;

import com.github.multidestroy.database.Database;

import java.time.Instant;

public class BanData {

    private final Database dataBase;
    public final int id;
    public final String recipient;
    public final int giverID;
    public final String reason;
    public final long time;
    public final long expiration;

    public BanData(Database dataBase, int id, String recipient, int giverID, String reason, long time, long expiration) {
        this.dataBase = dataBase;
        this.id = id;
        this.recipient = recipient;
        this.reason = reason;
        this.giverID = giverID;
        this.expiration = expiration;
        this.time = time;
    }

    public boolean isPerm() {
        return expiration == Long.MAX_VALUE;
    }

    public String getGiverID() {
        return dataBase.getPlayerName(giverID);
    }

    public Instant getTime() {
        return Instant.ofEpochMilli(time);
    }

    public Instant getExpiration() {
        return Instant.ofEpochMilli(expiration);
    }

}