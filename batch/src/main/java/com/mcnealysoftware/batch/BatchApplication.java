package com.mcnealysoftware.batch;

import org.postgresql.ds.PGSimpleDataSource;
import dao.EventDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class BatchApplication {
    static final Logger logger = Logger.getLogger(BatchApplication.class.getName());

    private final Connection connection;

    BatchApplication(Connection connection){
        this.connection = connection;
    }

    public static Connection getConnection() throws SQLException {
        final var source = new PGSimpleDataSource();
        source.setApplicationName("Batch Event Application");
        source.setServerNames(new String[]{"localhost"});
        source.setDatabaseName("streaming_architecture");
        return source.getConnection();
    }

    public int getOffsest() throws SQLException {
        var offset = 0;
        try(final var statement = connection.prepareStatement(
            "SELECT \"offset\" FROM batch_processed WHERE \"type\" = ?;")){
            statement.setString(1, "type");
            final var result = statement.executeQuery();
            if(result.next()){
                offset = result.getInt("offset");
            }
        }
        return offset;
    }

    public void markEventProcessed(int id) throws SQLException {
        try(var statement = connection.prepareStatement(
            "UPDATE event SET processed = true WHERE id = ?;")){
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public void writeOffset(int offset) throws SQLException {
        try(var statement = connection.prepareStatement(
            "INSERT INTO batch_processed (\"type\", \"offset\") VALUES (?, ?) " +
                "ON CONFLICT (\"type\") DO UPDATE SET \"offset\" = EXCLUDED.offset")){
            statement.setString(1, "type");
            statement.setInt(2, offset);
            statement.executeUpdate();
        }
    }

    public static void main(String[] args) {
        try(final var connection = getConnection()){
            var application = new BatchApplication(connection);
            var offset = application.getOffsest();
            logger.fine("Starting at offset " + offset);

            final var eventDao = new EventDao(connection);

            for (var event : eventDao.getSinceId(offset)) {
                offset = event.id();

                /*
                    Do processing here
                 */

                application.markEventProcessed(event.id());
                logger.info("processing event " + event.id());
            }

            application.writeOffset(offset);
            logger.fine("Wrote offset " + offset);
            logger.info("Done");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
