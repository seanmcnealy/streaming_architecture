package com.mcnealysoftware.stream.web;

import dao.EventDao;
import model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class EventsController {

    EventDao eventDao;

    @Autowired
    public EventsController(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @GetMapping("/")
    public ResponseEntity<Event> latest() {
        try {
            final var latest = eventDao.getLatest();
            if(latest == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(latest);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> get(@PathVariable("id") Integer id) {
        try{
            final var event = eventDao.get(id);
            if(event == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(event);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> insert(@RequestBody Event event) {
        if(event.type() == null || event.type().isBlank() || event.type().isEmpty())
            return ResponseEntity.badRequest().body("type must be valid");
        try {
            final var id = eventDao.insert(event);
            if(id == null)
                return ResponseEntity.badRequest().build();
            return ResponseEntity.status(HttpStatus.CREATED).body("/%d".formatted(id));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
