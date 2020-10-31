package com.github.multidestroy.environment.database;

import com.github.multidestroy.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static HikariDataSource dataSource;

    public DataSource(Config config) throws Exception {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:postgresql://"
                + config.get().getSection("database").getString("host") + "/" +
                config.get().getSection("database").getString("name"));
        hikariConfig.setUsername(config.get().getSection("database").getString("username"));
        hikariConfig.setPassword(config.get().getSection("database").getString("password"));
        hikariConfig.addDataSourceProperty( "cachePrepStmts" , "true" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSqlLimit" , "1024" );

        try {
            dataSource = new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }
}