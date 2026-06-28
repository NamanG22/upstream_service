package com.eventrouting.upstream.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class AmountGeneratorTest {

	private final AmountGenerator amountGenerator = new AmountGenerator();

	@RepeatedTest(50)
	void nextAmount_isWithinRange() {
		BigDecimal amount = new BigDecimal(amountGenerator.nextAmount());

		assertThat(amount).isBetween(new BigDecimal("0.01"), new BigDecimal("10000.00"));
		assertThat(amount.scale()).isLessThanOrEqualTo(2);
	}

	@Test
	void nextAmount_hasAtMostTwoDecimalPlaces() {
		String amount = amountGenerator.nextAmount();
		assertThat(amount).matches("\\d+(\\.\\d{1,2})?");
	}

}
