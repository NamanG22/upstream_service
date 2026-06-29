package com.eventrouting.upstream.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TransactionIdGeneratorTest {

	@TempDir
	Path tempDir;

	@Test
	void nextId_startsAtOneWhenCounterFileMissing() {
		TransactionIdGenerator generator = new TransactionIdGenerator(counterFilePath(tempDir));

		assertThat(generator.nextId()).isEqualTo("1");
		assertThat(generator.nextId()).isEqualTo("2");
	}

	@Test
	void nextId_resumesFromPersistedValueAfterRestart() throws Exception {
		Path counterFile = tempDir.resolve("transaction-id.counter");
		Files.createDirectories(counterFile.getParent());
		Files.writeString(counterFile, "42");

		TransactionIdGenerator generator = new TransactionIdGenerator(counterFile.toString());
		generator.loadCounter();

		assertThat(generator.nextId()).isEqualTo("43");
		assertThat(Files.readString(counterFile).trim()).isEqualTo("43");
	}

	private static String counterFilePath(Path tempDir) {
		return tempDir.resolve("transaction-id.counter").toString();
	}

}
