
package com.nimscreation.MyLinkedin.post_service.config;

import com.nimscreation.MyLinkedin.post_service.events.PostCreatedEvent;
import com.nimscreation.MyLinkedin.post_service.events.PostLikedEvent;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class kafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    private Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        config.put(
                JsonSerializer.ADD_TYPE_INFO_HEADERS,
                true
        );

        return config;
    }

    @Bean
    public NewTopic postCreated() {
        return new NewTopic("post_created_topic", 3, (short) 1);
    }

    @Bean
    public NewTopic postLiked() {
        return new NewTopic("post_liked_topic", 3, (short) 1);
    }

    @Bean
    public ProducerFactory<Long, PostCreatedEvent> postCreatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<Long, PostCreatedEvent> postCreatedKafkaTemplate() {
        return new KafkaTemplate<>(postCreatedProducerFactory());
    }

    @Bean
    public ProducerFactory<Long, PostLikedEvent> postLikedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<Long, PostLikedEvent> postLikedKafkaTemplate() {
        return new KafkaTemplate<>(postLikedProducerFactory());
    }
}
