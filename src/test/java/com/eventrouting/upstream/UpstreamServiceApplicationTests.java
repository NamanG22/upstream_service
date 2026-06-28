package com.eventrouting.upstream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "events" })
class UpstreamServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
