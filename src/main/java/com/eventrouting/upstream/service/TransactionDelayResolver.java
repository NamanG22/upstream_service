package com.eventrouting.upstream.service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.eventrouting.upstream.config.TransactionDelayProperties;
import com.eventrouting.upstream.config.TransactionDelayProperties.DelayRange;
import com.eventrouting.upstream.enums.PaymentMode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionDelayResolver {

	private final TransactionDelayProperties delayProperties;

	public Duration resolveDelay(PaymentMode paymentMode) {
		DelayRange range = switch (paymentMode) {
			case UPI_QR -> delayProperties.getUpiQr();
			case ONLINE_CHECKOUT -> delayProperties.getOnlineCheckout();
		};
		long delayMs = ThreadLocalRandom.current().nextLong(range.getMinMs(), range.getMaxMs() + 1);
		return Duration.ofMillis(delayMs);
	}

}
