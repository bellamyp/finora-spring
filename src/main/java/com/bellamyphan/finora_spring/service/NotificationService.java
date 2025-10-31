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

    public NotificationService(
            EmailService emailService,
            @Value("${notification.recipient}") String recipient
    ) {
        this.emailService = emailService;
        this.recipient = recipient;
    }

    // Send startup notification (called once when the app boots)
    public void sendStartupNotification() {
        emailService.sendEmail(
                recipient,
                "Finora Server Started",
                "✅ Finora Spring Boot application has started successfully!"
        );
        logger.info("Startup email sent.");
    }

    // Send periodic update every 24 hours (86400000 ms)
    @Scheduled(fixedRate = 86400000)
    public void sendDailyStatusNotification() {
        emailService.sendEmail(
                recipient,
                "Finora Daily Update",
                "☀️ Finora Spring Boot is still running smoothly after 24 hours!"
        );
        logger.info("Daily status email sent.");
    }
}
