package com.eventrouting.upstream.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import com.eventrouting.upstream.constants.EventConstants;
import com.eventrouting.upstream.dto.EventPublish;
import com.eventrouting.upstream.dto.TransactionRequest;
import com.eventrouting.upstream.enums.Currency;
import com.eventrouting.upstream.enums.EventType;
import com.eventrouting.upstream.enums.PaymentMode;
import com.eventrouting.upstream.enums.PaymentStatus;
import com.eventrouting.upstream.kafka.EventProducer;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private EventProducer eventProducer;

	@Mock
	private EventIdGenerator eventIdGenerator;

	@Mock
	private TransactionIdGenerator transactionIdGenerator;

	@Mock
	private AmountGenerator amountGenerator;

	@Mock
	private ParticipantIdGenerator participantIdGenerator;

	@Mock
	private TransactionDelayResolver transactionDelayResolver;

	@Mock
	private TaskScheduler taskScheduler;

	@InjectMocks
	private TransactionService transactionService;

	@Captor
	private ArgumentCaptor<EventPublish> eventCaptor;

	@Captor
	private ArgumentCaptor<Runnable> completionTaskCaptor;

	@BeforeEach
	void setUp() {
		when(transactionIdGenerator.nextId()).thenReturn("1001");
		when(amountGenerator.nextAmount()).thenReturn("250.00");
		when(participantIdGenerator.randomMerchantId()).thenReturn("1002");
		when(participantIdGenerator.randomCustomerId()).thenReturn("1003");
		when(transactionDelayResolver.resolveDelay(PaymentMode.UPI_QR)).thenReturn(Duration.ofSeconds(5));
		when(eventIdGenerator.nextId()).thenReturn("5001", "5002");
	}

	@Test
	void initiateTransaction_publishesPendingEventAndSchedulesCompletion() {
		TransactionRequest request = transactionRequest(PaymentMode.UPI_QR);

		transactionService.initiateTransaction(request);

		verify(taskScheduler).schedule(completionTaskCaptor.capture(), any(Instant.class));
		completionTaskCaptor.getValue().run();

		verify(eventProducer, times(2)).publish(eventCaptor.capture());
		EventPublish pendingEvent = eventCaptor.getAllValues().get(0);
		EventPublish terminalEvent = eventCaptor.getAllValues().get(1);
		assertThat(pendingEvent.getEventId()).isEqualTo("5001");
		assertThat(pendingEvent.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
		assertThat(pendingEvent.getTransactionId()).isEqualTo("1001");
		assertThat(pendingEvent.getMerchantId()).isEqualTo("1002");
		assertThat(pendingEvent.getCustomerId()).isEqualTo("1003");
		assertThat(pendingEvent.getAmount()).isEqualTo("250.00");
		assertThat(pendingEvent.getMetadata())
				.doesNotContainKey(EventConstants.METADATA_COMPLETION_DELAY_MS_KEY);

		assertThat(terminalEvent.getEventId()).isEqualTo("5002");
		assertThat(terminalEvent.getPaymentStatus()).isIn(PaymentStatus.SUCCESS, PaymentStatus.FAILED);
		assertThat(terminalEvent.getTransactionId()).isEqualTo("1001");
		assertThat(terminalEvent.getMerchantId()).isEqualTo("1002");
		assertThat(terminalEvent.getCustomerId()).isEqualTo("1003");
		assertThat(terminalEvent.getAmount()).isEqualTo("250.00");
		assertThat(terminalEvent.getMetadata())
				.containsEntry(EventConstants.METADATA_COMPLETION_DELAY_MS_KEY, "5000");
	}

	private static TransactionRequest transactionRequest(PaymentMode paymentMode) {
		TransactionRequest request = new TransactionRequest();
		request.setEventType(EventType.PAYMENT_STATUS_UPDATED);
		request.setPaymentMode(paymentMode);
		request.setCurrency(Currency.INR);
		Map<String, String> metadata = new HashMap<>();
		metadata.put("source", "mobile-app");
		request.setMetadata(metadata);
		return request;
	}

}
