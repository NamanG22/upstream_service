package com.eventrouting.upstream.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

class FileBackedCounter {

	private final Path counterFile;
	private final AtomicLong counter = new AtomicLong();

	FileBackedCounter(String counterFilePath) {
		this.counterFile = Path.of(counterFilePath);
	}

	void load() throws IOException {
		Files.createDirectories(counterFile.getParent());
		if (Files.exists(counterFile)) {
			String content = Files.readString(counterFile).trim();
			if (!content.isEmpty()) {
				counter.set(Long.parseLong(content));
			}
		}
	}

	synchronized long incrementAndGet() {
		long next = counter.incrementAndGet();
		try {
			Files.writeString(counterFile, Long.toString(next));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to persist counter file: " + counterFile, e);
		}
		return next;
	}

	synchronized void setAndPersist(long value) {
		counter.set(value);
		try {
			Files.writeString(counterFile, Long.toString(value));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to persist counter file: " + counterFile, e);
		}
	}

	long get() {
		return counter.get();
	}

}
