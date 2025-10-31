package com.bellamyphan.finora_spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // loads application-test.properties
class FinoraSpringApplicationTests {

    @Test
    void contextLoads() {
        // Real EmailService and NotificationService beans are loaded,
        // but emails won't actually go anywhere because host=localhost
    }
}
