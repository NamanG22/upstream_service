package com.eventrouting.upstream.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eventrouting.upstream.config.TransactionDelayProperties;
import com.eventrouting.upstream.config.TransactionDelayProperties.DelayRange;
import com.eventrouting.upstream.enums.PaymentMode;

class TransactionDelayResolverTest {

	@Test
	void resolveDelay_staysWithinUpiQrRange() {
		TransactionDelayProperties properties = new TransactionDelayProperties();
		properties.setUpiQr(new DelayRange(2_000, 120_000));
		properties.setOnlineCheckout(new DelayRange(10_000, 300_000));
		TransactionDelayResolver resolver = new TransactionDelayResolver(properties);

		for (int i = 0; i < 100; i++) {
			long delayMs = resolver.resolveDelay(PaymentMode.UPI_QR).toMillis();
			assertThat(delayMs).isBetween(2_000L, 120_000L);
		}
	}

	@Test
	void resolveDelay_staysWithinOnlineCheckoutRange() {
		TransactionDelayProperties properties = new TransactionDelayProperties();
		properties.setUpiQr(new DelayRange(2_000, 120_000));
		properties.setOnlineCheckout(new DelayRange(10_000, 300_000));
		TransactionDelayResolver resolver = new TransactionDelayResolver(properties);

		for (int i = 0; i < 100; i++) {
			long delayMs = resolver.resolveDelay(PaymentMode.ONLINE_CHECKOUT).toMillis();
			assertThat(delayMs).isBetween(10_000L, 300_000L);
		}
	}

}
