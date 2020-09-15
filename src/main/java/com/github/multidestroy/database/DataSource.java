package com.github.multidestroy.database;

import com.github.multidestroy.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Map;

public class DataSource {

    private static HikariDataSource dataSource;

    public DataSource(Config config) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:postgresql://"
                + config.get().getSection("database").getString("host") + "/" +
                config.get().getSection("database").getString("name"));
        hikariConfig.setUsername(config.get().getSection("database").getString("username"));
        hikariConfig.setPassword(config.get().getSection("database").getString("password"));
        hikariConfig.addDataSourceProperty( "cachePrepStmts" , "true" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSqlLimit" , "1024" );


        dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}