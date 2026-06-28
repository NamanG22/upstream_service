package com.eventrouting.upstream.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.eventrouting.upstream.constants.ConfigConstants;

class ParticipantIdGeneratorTest {

	@TempDir
	Path tempDir;

	@RepeatedTest(30)
	void randomMerchantId_isWithinConfiguredRange() throws Exception {
		ParticipantIdGenerator generator = createGeneratorWithMax(tempDir, "1006");

		long merchantId = Long.parseLong(generator.randomMerchantId());

		assertThat(merchantId).isBetween(ConfigConstants.MIN_PARTICIPANT_ID, 1007L);
	}

	@RepeatedTest(30)
	void randomCustomerId_isWithinConfiguredRange() throws Exception {
		ParticipantIdGenerator generator = createGeneratorWithMax(tempDir, "1006");

		long customerId = Long.parseLong(generator.randomCustomerId());

		assertThat(customerId).isBetween(ConfigConstants.MIN_PARTICIPANT_ID, 1007L);
	}

	@Test
	void randomId_usesDefaultMaxWhenCounterFileMissing() throws Exception {
		ParticipantIdGenerator generator = new ParticipantIdGenerator(
				counterFilePath(tempDir, "merchant-id.counter"),
				counterFilePath(tempDir, "customer-id.counter"));
		generator.loadCounters();

		long merchantId = Long.parseLong(generator.randomMerchantId());

		assertThat(merchantId).isBetween(ConfigConstants.MIN_PARTICIPANT_ID, 1007L);
	}

	@Test
	void expandMaxIfNeeded_persistsNewMaxWhenGeneratedIdIsMaxPlusOne() throws Exception {
		Path merchantCounter = tempDir.resolve("merchant-id.counter");
		Files.writeString(merchantCounter, "1006");

		FileBackedCounter counter = new FileBackedCounter(merchantCounter.toString());
		counter.load();

		ParticipantIdGenerator generator = new ParticipantIdGenerator(
				merchantCounter.toString(),
				tempDir.resolve("customer-id.counter").toString());
		generator.expandMaxIfNeeded(counter, 1006, 1007);

		assertThat(counter.get()).isEqualTo(1007);
		assertThat(Files.readString(merchantCounter).trim()).isEqualTo("1007");
	}

	@Test
	void expandMaxIfNeeded_doesNotUpdateWhenGeneratedIdIsWithinExistingRange() throws Exception {
		Path merchantCounter = tempDir.resolve("merchant-id.counter");
		Files.writeString(merchantCounter, "1006");

		FileBackedCounter counter = new FileBackedCounter(merchantCounter.toString());
		counter.load();

		ParticipantIdGenerator generator = new ParticipantIdGenerator(
				merchantCounter.toString(),
				tempDir.resolve("customer-id.counter").toString());
		generator.expandMaxIfNeeded(counter, 1006, 1003);

		assertThat(counter.get()).isEqualTo(1006);
		assertThat(Files.readString(merchantCounter).trim()).isEqualTo("1006");
	}

	private static ParticipantIdGenerator createGeneratorWithMax(Path tempDir, String maxValue) throws Exception {
		Path merchantCounter = tempDir.resolve("merchant-id.counter");
		Path customerCounter = tempDir.resolve("customer-id.counter");
		Files.createDirectories(tempDir);
		Files.writeString(merchantCounter, maxValue);
		Files.writeString(customerCounter, maxValue);

		ParticipantIdGenerator generator = new ParticipantIdGenerator(
				merchantCounter.toString(),
				customerCounter.toString());
		generator.loadCounters();
		return generator;
	}

	private static String counterFilePath(Path tempDir, String fileName) {
		return tempDir.resolve(fileName).toString();
	}

}
