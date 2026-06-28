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
		Path counterFile = Path.of(System.getProperty("java.io.tmpdir"), "upstream-service-test", "event-id.counter");
		registry.add("app.event-id.counter-file", counterFile::toString);
	}

	@Test
	void contextLoads() {
	}

}
