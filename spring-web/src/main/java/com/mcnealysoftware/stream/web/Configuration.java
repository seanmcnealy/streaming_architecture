package com.mcnealysoftware.stream.web;

import dao.EventDao;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean Connection connection(DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }

    @Bean
    EventDao eventDao(Connection connection) {
        return new EventDao(connection);
    }
}
