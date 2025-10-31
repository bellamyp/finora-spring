package com.bellamyphan.finora_spring;

import com.bellamyphan.finora_spring.service.EmailService;
import com.bellamyphan.finora_spring.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // ensures it's treated as non-production
class FinoraSpringApplicationTests {

    @Configuration
    static class TestConfig {

        // Provide a no-op EmailService for tests
        @Bean
        @Primary
        EmailService emailService() {
            return new EmailService(null) {
                @Override
                public void sendEmail(String to, String subject, String text) {
                    // do nothing
                }
            };
        }

        // Provide NotificationService with dummy recipient
        @Bean
        @Primary
        NotificationService notificationService(EmailService emailService) {
            return new NotificationService(
                    emailService,
                    "test@example.com",
                    "test" // appEnv safely set for test context
            );
        }
    }

    @Test
    void contextLoads() {
        // Ensures Spring context loads successfully
        // This covers NotificationService + EmailService initialization
    }
}
