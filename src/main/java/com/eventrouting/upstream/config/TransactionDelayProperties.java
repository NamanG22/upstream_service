package com.eventrouting.upstream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.transaction.delay")
public class TransactionDelayProperties {

	private DelayRange upiQr = new DelayRange(2_000, 120_000);
	private DelayRange onlineCheckout = new DelayRange(10_000, 300_000);

	@Getter
	@Setter
	public static class DelayRange {
		private long minMs;
		private long maxMs;

		public DelayRange() {
		}

		public DelayRange(long minMs, long maxMs) {
			this.minMs = minMs;
			this.maxMs = maxMs;
		}
	}

}
