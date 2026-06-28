package com.eventrouting.upstream.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class EventIdGenerator {

	private final Path counterFile;
	private final AtomicLong counter = new AtomicLong();

	public EventIdGenerator(@Value("${app.event-id.counter-file}") String counterFilePath) {
		this.counterFile = Path.of(counterFilePath);
	}

	@PostConstruct
	void loadCounter() throws IOException {
		Files.createDirectories(counterFile.getParent());
		if (Files.exists(counterFile)) {
			String content = Files.readString(counterFile).trim();
			if (!content.isEmpty()) {
				counter.set(Long.parseLong(content));
			}
		}
	}

	public synchronized String nextId() {
		long next = counter.incrementAndGet();
		try {
			Files.writeString(counterFile, Long.toString(next));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to persist event id counter", e);
		}
		return Long.toString(next);
	}

}
