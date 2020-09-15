package com.github.multidestroy.threads;

import com.github.multidestroy.database.Database;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class DeleteExpiredBans extends Thread {

    private final Database dataBase;
    private final String serverName;
    private static final long minimumBanTime = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);

    public DeleteExpiredBans(Database dataBase, String serverName) {
        this.dataBase = dataBase;
        this.serverName = serverName;
    }

    @Override
    public void run() {
        while (true) {
            Instant now = Instant.now();
            Instant wakeUpTime = dataBase.removeExpiredBans(serverName, now);
            long timeToSleep = getTimeToSleep(now, wakeUpTime);

            try {
                sleep(timeToSleep);
            } catch (InterruptedException ignored) {}
        }
    }


    private long getTimeToSleep(Instant now, Instant expirationDate) {
        if(expirationDate == null)
            return minimumBanTime;

        long mSecondBetween = TimeUnit.MILLISECONDS.convert(Duration.between(now, expirationDate).getSeconds() + 1, TimeUnit.SECONDS);
        return Math.min(mSecondBetween, minimumBanTime);
    }
}
