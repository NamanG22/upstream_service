package com.eventrouting.upstream.kafka;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.eventrouting.upstream.dto.EventPublish;
import com.eventrouting.upstream.dto.EventRequest;

import tools.jackson.databind.ObjectMapper;

@Service
public class EventProducer {

	private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
	private static final DateTimeFormatter TIMESTAMP_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

	public void publish(EventRequest event) {
		String key = event.getEventType() != null ? event.getEventType().name() : null;
		try {
			EventPublish eventPublish = convertToEventPublish(event);
			String payload = objectMapper.writeValueAsString(eventPublish);
			kafkaTemplate.send(eventsTopic, key, payload);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to publish event to Kafka", e);
		}
	}

	private EventPublish convertToEventPublish(EventRequest event) {
		EventPublish eventPublish = new EventPublish();
		eventPublish.setEventId(event.getEventId());
		eventPublish.setPaymentMode(event.getPaymentMode());
		eventPublish.setPaymentStatus(event.getPaymentStatus());
		eventPublish.setMerchantId(event.getMerchantId());
		eventPublish.setCustomerId(event.getCustomerId());
		eventPublish.setTransactionId(event.getTransactionId());
		eventPublish.setAmount(event.getAmount());
		eventPublish.setCurrency(event.getCurrency());
		eventPublish.setMetadata(event.getMetadata());
		eventPublish.setEventType(event.getEventType());
		eventPublish.setTimestamp(ZonedDateTime.now(IST).format(TIMESTAMP_FORMAT));
		return eventPublish;
	}
}
