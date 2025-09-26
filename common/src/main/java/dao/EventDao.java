package dao;

import model.Event;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EventDao {
    private final DataSource dataSource;

    public EventDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Integer insert(Event event) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            try (final var statement = connection.prepareStatement(
                    "INSERT INTO event (\"type\", \"message\") VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, event.type());
                statement.setString(2, event.message());
                final var updated = statement.executeUpdate();
                if (updated > 0) {
                    final var generatedKeys = statement.getGeneratedKeys();
                    generatedKeys.next();
                    return generatedKeys.getInt(1);
                }
            }
            connection.commit();
        }
        return null;
    }

    public Event get(Integer id) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            try (final var statement = connection.prepareStatement(
                    "SELECT id, type, message, timestamp FROM event WHERE id = ?;"
            )) {
                statement.setInt(1, id);
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return transform(resultSet);
                    }
                }
            }
        }
        return null;
    }

    public Event getLatest() throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            try (final var statement = connection.prepareStatement(
                    "SELECT id, type, message, timestamp FROM event ORDER BY id DESC LIMIT 1;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return transform(resultSet);
                    }
                }
            }
        }
        return null;
    }

    public List<Event> getSinceId(Integer id) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            try (final var statement = connection.prepareStatement(
                    "SELECT id, type, message, timestamp FROM event WHERE id > ?;"
            )) {
                statement.setInt(1, id);
                try (var resultSet = statement.executeQuery()) {
                    final var resultList = new ArrayList<Event>();
                    while (resultSet.next()) {
                        resultList.add(transform(resultSet));
                    }
                    return resultList;
                }
            }
        }
    }

    private Event transform(ResultSet resultSet) throws SQLException {
        return new Event(
                resultSet.getInt("id"),
                resultSet.getString("type"),
                resultSet.getString("message"),
                resultSet.getTimestamp("timestamp"));
    }
}
