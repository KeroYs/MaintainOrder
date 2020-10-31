package com.github.multidestroy.environment.database;

public enum SQLQueries {

    CREATE_BAN_TABLE("CREATE TABLE IF NOT EXISTS bans ("
            + "id SERIAL PRIMARY KEY,"
            + "server_id INT,"
            + "recipient VARCHAR(17),"
            + "giver INT,"
            + "reason VARCHAR(300),"
            + "time BIGINT,"
            + "expiration BIGINT)");

    private final String query;


    SQLQueries(String query) {
        this.query = query;
    }
}
