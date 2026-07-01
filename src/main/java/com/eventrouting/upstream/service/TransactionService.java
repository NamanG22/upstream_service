package com.eventrouting.upstream.service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.eventrouting.upstream.constants.ConfigConstants;
import com.eventrouting.upstream.constants.EventConstants;
import com.eventrouting.upstream.dto.EventPublish;
import com.eventrouting.upstream.dto.TransactionRequest;
import com.eventrouting.upstream.enums.PaymentStatus;
import com.eventrouting.upstream.kafka.EventProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
	private static final DateTimeFormatter TIMESTAMP_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final EventProducer eventProducer;
	private final EventIdGenerator eventIdGenerator;
	private final TransactionIdGenerator transactionIdGenerator;
	private final AmountGenerator amountGenerator;
	private final ParticipantIdGenerator participantIdGenerator;
	private final TransactionDelayResolver transactionDelayResolver;
	private final TaskScheduler taskScheduler;

	public void initiateTransaction(TransactionRequest request) {
		TransactionSnapshot snapshot = buildSnapshot(request);
		Duration completionDelay = transactionDelayResolver.resolveDelay(request.getPaymentMode());
		publishEvent(snapshot, PaymentStatus.PENDING);
		taskScheduler.schedule(
				() -> completeTransaction(snapshot, completionDelay),
				Instant.now().plus(completionDelay));
	}

	private void completeTransaction(TransactionSnapshot snapshot, Duration completionDelay) {
		PaymentStatus terminalStatus = ThreadLocalRandom.current().nextBoolean()
				? PaymentStatus.SUCCESS
				: PaymentStatus.FAILED;
		publishEvent(snapshot, terminalStatus, completionDelay);
	}

	private TransactionSnapshot buildSnapshot(TransactionRequest request) {
		return new TransactionSnapshot(
				transactionIdGenerator.nextId(),
				request.getEventType(),
				request.getPaymentMode(),
				participantIdGenerator.randomMerchantId(),
				participantIdGenerator.randomCustomerId(),
				amountGenerator.nextAmount(),
				request.getCurrency(),
				enrichMetadata(request.getMetadata()));
	}

	private Map<String, String> enrichMetadata(Map<String, String> metadata) {
		Map<String, String> enriched = metadata == null ? new HashMap<>(2) : new HashMap<>(metadata);
		enriched.put(EventConstants.METADATA_TEMPLATE_ID_KEY, ConfigConstants.TEMPLATE_ID);
		return enriched;
	}

	private void publishEvent(TransactionSnapshot snapshot, PaymentStatus paymentStatus) {
		publishEvent(snapshot, paymentStatus, null);
	}

	private void publishEvent(TransactionSnapshot snapshot, PaymentStatus paymentStatus, Duration completionDelay) {
		Map<String, String> metadata = snapshot.metadata();
		if (completionDelay != null) {
			metadata.put(EventConstants.METADATA_COMPLETION_DELAY_MS_KEY, Long.toString(completionDelay.toMillis()));
		}

		EventPublish eventPublish = new EventPublish();
		eventPublish.setUpstreamId(ConfigConstants.UPSTREAM_ID);
		eventPublish.setEventId(eventIdGenerator.nextId());
		eventPublish.setEventType(snapshot.eventType());
		eventPublish.setPaymentMode(snapshot.paymentMode());
		eventPublish.setPaymentStatus(paymentStatus);
		eventPublish.setMerchantId(snapshot.merchantId());
		eventPublish.setCustomerId(snapshot.customerId());
		eventPublish.setTransactionId(snapshot.transactionId());
		eventPublish.setAmount(snapshot.amount());
		eventPublish.setCurrency(snapshot.currency());
		eventPublish.setMetadata(metadata);
		eventPublish.setTimestamp(ZonedDateTime.now(IST).format(TIMESTAMP_FORMAT));
		eventProducer.publish(eventPublish);
	}

}
