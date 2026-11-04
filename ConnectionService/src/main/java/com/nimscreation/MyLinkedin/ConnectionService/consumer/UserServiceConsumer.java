
package com.nimscreation.MyLinkedin.ConnectionService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimscreation.MyLinkedin.ConnectionService.services.PersonService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceConsumer {

    private final PersonService personService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user_created_topic")
    public void handlePersonCreated(String message)
            throws JsonProcessingException {

        log.info("Received Kafka message: {}", message);

        UserCreatedEvent userCreatedEvent =
                objectMapper.readValue(message, UserCreatedEvent.class);

        log.info("Deserialized UserCreatedEvent: {}", userCreatedEvent);

        personService.createPerson(
                userCreatedEvent.getUserId(),
                userCreatedEvent.getName()
        );
    }
}
