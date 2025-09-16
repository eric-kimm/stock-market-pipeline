package com.example.springboot.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

// Create a new kafka topic
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic stockTickTopic() {
        return TopicBuilder.name("stock-trades")
                .build();
    }

    @Bean
    public NewTopic testing() {
        return TopicBuilder.name("testing")
                .build();
    }
}
