package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import model.Event;
import org.flywaydb.core.Flyway;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.PostgreSQLContainer;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class EventDaoTest {
    @FunctionalInterface
    public interface CheckedConsumer<T> {
        void accept(T t) throws SQLException;
    }

    private void setup(CheckedConsumer<Connection> f) throws SQLException {
        try (final var postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))) {
            postgres.start();
            final var config = new HikariConfig();
            config.setJdbcUrl(postgres.getJdbcUrl());
            config.setUsername(postgres.getUsername());
            config.setPassword(postgres.getPassword());
            config.setDriverClassName(postgres.getDriverClassName());
            try (var datasource = new HikariDataSource(config)) {
                final var flyway = Flyway.configure()
                    .dataSource(datasource)
                    .locations("classpath:schema")
                    .load();
                flyway.migrate();
                f.accept(datasource.getConnection());
            }
        }
    }

    @Test
    void insertTest() throws SQLException {
        setup(connection -> {
            final var dao = new EventDao(connection);

            final var id = dao.insert(new Event("test", "message"));
            assertEquals(1, id);
        });
    }

    @Test
    void retrieveTest() throws SQLException {
        setup(connection -> {
            final var dao = new EventDao(connection);

            dao.insert(new Event("test1", "message1"));
            final var id = dao.insert(new Event("test2", "message2"));
            dao.insert(new Event("test3", "message3"));

            final var retrieved = dao.get(id);
            assertEquals("test2", retrieved.type());
        });
    }

    @Test
    void getAllAfterTest() throws SQLException {
        setup(connection -> {
            final var dao = new EventDao(connection);

            dao.insert(new Event("test1", "message1"));
            dao.insert(new Event("test2", "message2"));
            dao.insert(new Event("test3", "message3"));

            final var retrieved = dao.getSinceId(1);
            assertEquals("test2", retrieved.get(0).type());
            assertEquals("test3", retrieved.get(1).type());
        });
    }

    @Test
    void getLatestTest() throws SQLException {
        setup(connection -> {
            final var dao = new EventDao(connection);

            dao.insert(new Event("test1", "message1"));
            dao.insert(new Event("test2", "message2"));
            dao.insert(new Event("test3", "message3"));

            final var retrieved = dao.getLatest();
            assertEquals("test3", retrieved.type());
        });
    }
}
