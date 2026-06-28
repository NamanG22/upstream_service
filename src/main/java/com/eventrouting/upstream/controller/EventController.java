package com.eventrouting.upstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eventrouting.upstream.dto.EventRequest;
import com.eventrouting.upstream.kafka.EventProducer;

@RestController
public class EventController {

	private final EventProducer eventProducer;

	public EventController(EventProducer eventProducer) {
		this.eventProducer = eventProducer;
	}

	@PostMapping("/events")
	public ResponseEntity<Void> processEvent(@RequestBody EventRequest event) {
		eventProducer.publish(event);
		return ResponseEntity.accepted().build();
	}

}
