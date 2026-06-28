package com.eventrouting.upstream.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class EventIdGenerator {

	private final FileBackedCounter counter;

	public EventIdGenerator(@Value("${app.event-id.counter-file}") String counterFilePath) {
		this.counter = new FileBackedCounter(counterFilePath);
	}

	@PostConstruct
	void loadCounter() throws Exception {
		counter.load();
	}

	public String nextId() {
		return Long.toString(counter.incrementAndGet());
	}

}
