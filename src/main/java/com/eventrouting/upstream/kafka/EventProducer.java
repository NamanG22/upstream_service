package com.eventrouting.upstream.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.eventrouting.upstream.dto.EventPublish;

import tools.jackson.databind.ObjectMapper;

@Service
public class EventProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final String eventsTopic;

	public EventProducer(
			KafkaTemplate<String, String> kafkaTemplate,
			ObjectMapper objectMapper,
			@Value("${app.kafka.events-topic}") String eventsTopic) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.eventsTopic = eventsTopic;
	}

	public void publish(EventPublish eventPublish) {
		String key = eventPublish.getEventType() != null ? eventPublish.getEventType().name() : null;
		try {
			String payload = objectMapper.writeValueAsString(eventPublish);
			kafkaTemplate.send(eventsTopic, key, payload);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to publish event to Kafka", e);
		}
	}
}
