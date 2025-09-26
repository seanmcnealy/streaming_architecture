package com.mcnealysoftware.stream;

import dao.EventDao;
import model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import java.sql.SQLException;

public class Listener {
    Logger logger = LoggerFactory.getLogger(Listener.class);

    EventDao eventDao;

    Listener(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @KafkaListener(id = "event-importer", topics = {"events"})
    public void accept(Event event) throws SQLException {
        logger.info("Processed event type {} message {}", event.type(), event.message());

        // this is a great place to insert into a database if the web api
        // didn't already do that
        eventDao.insert(event);

        /*
            Do stuff here, like aggregate, send emails.
            Though side effects outside kafka
            may happen more than once.
         */

        logger.info("Processed event type {} message {}", event.type(), event.message());
    }
}
