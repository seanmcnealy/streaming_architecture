package com.mcnealysoftware.stream;

import dao.EventDao;
import model.Event;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@org.springframework.context.annotation.Configuration
@EnableKafka
public class Configuration {

    @Bean Connection connection(DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }

    @Bean
    EventDao eventDao(Connection connection) {
        return new EventDao(connection);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Event>
    kafkaListenerContainerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092");
        final var consumerFactory = new DefaultKafkaConsumerFactory<String, Event>(configProps,
            new StringDeserializer(),
            new JsonDeserializer<>(Event.class, false));

        ConcurrentKafkaListenerContainerFactory<String, Event> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public Listener listener(EventDao eventDao) {
        return new Listener(eventDao);
    }
}
