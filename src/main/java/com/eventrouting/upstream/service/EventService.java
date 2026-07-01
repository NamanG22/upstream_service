package com.eventrouting.upstream.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.eventrouting.upstream.constants.ConfigConstants;
import com.eventrouting.upstream.constants.EventConstants;
import com.eventrouting.upstream.dto.EventPublish;
import com.eventrouting.upstream.dto.EventRequest;
import com.eventrouting.upstream.kafka.EventProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventProducer eventProducer;
    private final EventIdGenerator eventIdGenerator;
    private final AmountGenerator amountGenerator;
    private final ParticipantIdGenerator participantIdGenerator;

    public void processEvent(EventRequest event) {
        event.setEventId(eventIdGenerator.nextId());
        if (event.getAmount() == null || event.getAmount().isBlank()) {
            event.setAmount(amountGenerator.nextAmount());
        }
        if (event.getMerchantId() == null || event.getMerchantId().isBlank()) {
            event.setMerchantId(participantIdGenerator.randomMerchantId());
        }
        if (event.getCustomerId() == null || event.getCustomerId().isBlank()) {
            event.setCustomerId(participantIdGenerator.randomCustomerId());
        }
        enrichMetadata(event);
        EventPublish eventPublish = convertToEventPublish(event);
        eventProducer.publish(eventPublish);
    }

    private void enrichMetadata(EventRequest event) {
        Map<String, String> metadata = event.getMetadata();
        if (metadata == null) {
            metadata = new HashMap<>(1);
            event.setMetadata(metadata);
        }
        metadata.put(EventConstants.METADATA_TEMPLATE_ID_KEY, ConfigConstants.TEMPLATE_ID);
    }

    private EventPublish convertToEventPublish(EventRequest event) {
		EventPublish eventPublish = new EventPublish();
		eventPublish.setUpstreamId(ConfigConstants.UPSTREAM_ID);
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
