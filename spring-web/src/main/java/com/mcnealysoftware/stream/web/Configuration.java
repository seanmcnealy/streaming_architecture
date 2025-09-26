package com.mcnealysoftware.stream.web;

import dao.EventDao;
import model.Event;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    EventDao eventDao(DataSource dataSource) {
        return new EventDao(dataSource);
    }

    @Bean
    Producer<String, Event> eventClient() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        return new DefaultKafkaProducerFactory<String, Event>(configProps,
                new StringSerializer(),
                new JsonSerializer<>()).createProducer();
    }
}
