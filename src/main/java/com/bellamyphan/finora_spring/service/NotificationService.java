package com.bellamyphan.finora_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final EmailService emailService;
    private final String recipient;
    private final String appEnv;

    public NotificationService(
            EmailService emailService,
            @Value("${notification.recipient}") String recipient,
            @Value("${app.env:local}") String appEnv // default to local if not set
    ) {
        this.emailService = emailService;
        this.recipient = recipient;
        this.appEnv = appEnv;
    }

    // Send startup notification (called once when the app boots)
    public void sendStartupNotification() {
        if (isProduction()) {
            logger.info("Skipping startup email in production environment.");
            return;
        }

        try {
            emailService.sendEmail(
                    recipient,
                    "Finora Server Started",
                    "✅ Finora Spring Boot application has started successfully!"
            );
            logger.info("Startup email sent.");
        } catch (Exception ex) {
            logger.warn("❌ Failed to send startup email: {}", ex.getMessage());
        }
    }

    // Send periodic update every 24 hours (86400000 ms)
    @Scheduled(fixedRate = 86400000)
    public void sendDailyStatusNotification() {
        if (isProduction()) {
            logger.info("Skipping daily email in production environment.");
            return;
        }

        try {
            emailService.sendEmail(
                    recipient,
                    "Finora Daily Update",
                    "☀️ Finora Spring Boot is still running smoothly after 24 hours!"
            );
            logger.info("Daily status email sent.");
        } catch (Exception ex) {
            logger.warn("❌ Failed to send daily status email: {}", ex.getMessage());
        }
    }

    private boolean isProduction() {
        return "production".equalsIgnoreCase(appEnv);
    }
}
