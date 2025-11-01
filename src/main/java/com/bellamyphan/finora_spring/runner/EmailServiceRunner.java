package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.config.AppEnvironmentInfo;
import com.bellamyphan.finora_spring.service.NotificationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailServiceRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceRunner.class);
    private final NotificationService notificationService;
    private final AppEnvironmentInfo appEnvironmentInfo;

    @Override
    public void run(String... args) {

        logger.info("====== ENVIRONMENT INFO TEST RUNNER ======");
        try {
            String info = appEnvironmentInfo.buildInfo();
            System.out.println("\n===== Environment Info =====\n" + info + "\n============================\n");
            logger.info("Environment Info:\n{}", info);
        } catch (Exception ex) {
            logger.error("Failed to gather environment info: {}", ex.getMessage(), ex);
        }
        logger.info("==========================================");

        try {
            notificationService.sendStartupNotification();
            logger.info("Startup notification sent successfully.");
        } catch (Exception ex) {
            logger.warn("Failed to send startup email notification: {}", ex.getMessage());
        }
    }
}
