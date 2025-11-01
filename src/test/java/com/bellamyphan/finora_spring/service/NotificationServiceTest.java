package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.config.AppEnvironmentInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private AppEnvironmentInfo appEnvironmentInfo;

    private NotificationService notificationService;

    private final String recipient = "test@example.com";

    @BeforeEach
    void setUp() {
        when(appEnvironmentInfo.buildInfo()).thenReturn("Host: test-host\nProfile: test");

        notificationService = new NotificationService(
                emailService,
                appEnvironmentInfo,
                recipient,
                "test" // not production
        );
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
        String body = textCaptor.getValue();
        // Now includes environment info
        assertEquals(
                "✅ Finora Spring Boot application has started successfully!\n\n---\nHost: test-host\nProfile: test",
                body
        );
    }

    @Test
    void sendDailyStatusNotification_shouldCallEmailServiceWithCorrectArguments() {
        // Act
        notificationService.sendDailyStatusNotification();

        // Assert
        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(toCaptor.capture(), subjectCaptor.capture(), textCaptor.capture());

        assertEquals(recipient, toCaptor.getValue());
        assertEquals("Finora Daily Update", subjectCaptor.getValue());
        String body = textCaptor.getValue();
        assertEquals(
                "☀️ Finora Spring Boot is still running smoothly after 24 hours!\n\n---\nHost: test-host\nProfile: test",
                body
        );
    }
}
