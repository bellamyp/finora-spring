package com.bellamyphan.finora_spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    private NotificationService notificationService;

    private final String recipient = "test@example.com";

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(emailService, recipient);
    }

    @Test
    void sendStartupNotification_shouldCallEmailServiceWithCorrectArguments() {
        // Act
        notificationService.sendStartupNotification();

        // Assert
        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(toCaptor.capture(), subjectCaptor.capture(), textCaptor.capture());

        assertEquals(recipient, toCaptor.getValue());
        assertEquals("Finora Server Started", subjectCaptor.getValue());
        assertEquals("✅ Finora Spring Boot application has started successfully!", textCaptor.getValue());
    }

    // Todo: temporary disable this test.
    @Test
    void sendDailyStatusNotification_shouldCallEmailServiceWithCorrectArguments() {
//        // Act
//        notificationService.sendDailyStatusNotification();

//        // Assert
//        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
//
//        verify(emailService).sendEmail(toCaptor.capture(), subjectCaptor.capture(), textCaptor.capture());
//
//        assertEquals(recipient, toCaptor.getValue());
//        assertEquals("Finora Daily Update", subjectCaptor.getValue());
//        assertEquals("☀️ Finora Spring Boot is still running smoothly after 24 hours!", textCaptor.getValue());
    }
}
