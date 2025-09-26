package model;

import java.sql.Timestamp;

public record Event(
    Integer id,
    String type,
    String message,
    Timestamp timestamp
) {
    public Event(String type, String message) {
        this(null, type, message, null);
    }

}
