package com.bellamyphan.finora_spring;

import com.bellamyphan.finora_spring.config.AppEnvironmentInfo;
import com.bellamyphan.finora_spring.service.EmailService;
import com.bellamyphan.finora_spring.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
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

        // Mock or simplified AppEnvironmentInfo for test
        @Bean
        @Primary
        AppEnvironmentInfo appEnvironmentInfo(Environment environment) {
            return new AppEnvironmentInfo(environment) {
                @Override
                public String buildInfo() {
                    return "Test host info";
                }
            };
        }

        @Bean
        @Primary
        NotificationService notificationService(
                EmailService emailService,
                AppEnvironmentInfo appEnvironmentInfo
        ) {
            return new NotificationService(
                    emailService,
                    appEnvironmentInfo,
                    "test@example.com",
                    "test" // safe non-production env
            );
        }
    }

    @Test
    void contextLoads() {
        // Ensures Spring context loads successfully
        // This covers NotificationService + EmailService initialization
    }
}
