package com.andersenlab.dao.conection;

import com.andersenlab.config.ConfigData.PostgresDB;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private final HikariDataSource dataSource;

    public ConnectionPool(PostgresDB configDB) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configDB.getUrl());
        config.setUsername(configDB.getUsername());
        config.setPassword(configDB.getPassword());
        config.setMaximumPoolSize(configDB.getPool());

        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
