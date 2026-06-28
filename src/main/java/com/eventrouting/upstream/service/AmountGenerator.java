package com.eventrouting.upstream.service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

@Service
public class AmountGenerator {

	private static final long MIN_CENTS = 1;
	private static final long MAX_CENTS = 1_000_000;

	public String nextAmount() {
		long cents = ThreadLocalRandom.current().nextLong(MIN_CENTS, MAX_CENTS + 1);
		return BigDecimal.valueOf(cents, 2).toPlainString();
	}

}
