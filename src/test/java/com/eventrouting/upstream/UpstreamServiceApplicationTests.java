package com.eventrouting.upstream;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "events" })
class UpstreamServiceApplicationTests {

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		Path testDir = Path.of(System.getProperty("java.io.tmpdir"), "upstream-service-test");
		registry.add("app.event-id.counter-file", () -> testDir.resolve("event-id.counter").toString());
		registry.add("app.merchant-id.counter-file", () -> testDir.resolve("merchant-id.counter").toString());
		registry.add("app.customer-id.counter-file", () -> testDir.resolve("customer-id.counter").toString());
	}

	@Test
	void contextLoads() {
	}

}
