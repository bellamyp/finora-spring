package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.config.AppEnvironmentInfo;
import com.bellamyphan.finora_spring.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceRunnerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private AppEnvironmentInfo appEnvironmentInfo;

    @Test
    void run_shouldCallNotificationService() {
        doNothing().when(notificationService).sendStartupNotification();
        when(appEnvironmentInfo.buildInfo()).thenReturn("Mocked environment info");

        EmailServiceRunner runner = new EmailServiceRunner(notificationService, appEnvironmentInfo);

        runner.run();

        verify(notificationService).sendStartupNotification();
    }

    @Test
    void run_shouldHandleExceptionGracefully() {
        doThrow(new RuntimeException("SMTP error")).when(notificationService).sendStartupNotification();
        when(appEnvironmentInfo.buildInfo()).thenReturn("Mocked environment info");

        EmailServiceRunner runner = new EmailServiceRunner(notificationService, appEnvironmentInfo);

        // Should not throw even if email fails
        runner.run();

        verify(notificationService).sendStartupNotification();
    }
}
