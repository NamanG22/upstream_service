package com.eventrouting.upstream.service;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventrouting.upstream.constants.ConfigConstants;

import jakarta.annotation.PostConstruct;

@Service
public class ParticipantIdGenerator {

	private final FileBackedCounter merchantMaxCounter;
	private final FileBackedCounter customerMaxCounter;

	public ParticipantIdGenerator(
			@Value("${app.merchant-id.counter-file}") String merchantCounterFile,
			@Value("${app.customer-id.counter-file}") String customerCounterFile) {
		this.merchantMaxCounter = new FileBackedCounter(merchantCounterFile);
		this.customerMaxCounter = new FileBackedCounter(customerCounterFile);
	}

	@PostConstruct
	void loadCounters() throws IOException {
		merchantMaxCounter.load();
		customerMaxCounter.load();
	}

	public String randomMerchantId() {
		return randomId(merchantMaxCounter);
	}

	public String randomCustomerId() {
		return randomId(customerMaxCounter);
	}

	private synchronized String randomId(FileBackedCounter maxCounter) {
		long max = effectiveMax(maxCounter);
		long id = ThreadLocalRandom.current().nextLong(ConfigConstants.MIN_PARTICIPANT_ID, max + 2);
		expandMaxIfNeeded(maxCounter, max, id);
		return Long.toString(id);
	}

	void expandMaxIfNeeded(FileBackedCounter maxCounter, long max, long id) {
		if (id == max + 1) {
			maxCounter.setAndPersist(id);
		}
	}

	private long effectiveMax(FileBackedCounter maxCounter) {
		long max = maxCounter.get();
		return max > 0 ? max : ConfigConstants.DEFAULT_MAX_PARTICIPANT_ID;
	}

}
