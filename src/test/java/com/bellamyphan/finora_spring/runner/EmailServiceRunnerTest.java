package com.bellamyphan.finora_spring.runner;

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

    @Test
    void run_shouldCallNotificationService() throws Exception {
        doNothing().when(notificationService).sendStartupNotification();

        EmailServiceRunner runner = new EmailServiceRunner(notificationService);

        runner.run();

        verify(notificationService).sendStartupNotification();
    }

    @Test
    void run_shouldHandleExceptionGracefully() throws Exception {
        doThrow(new RuntimeException("SMTP error")).when(notificationService).sendStartupNotification();

        EmailServiceRunner runner = new EmailServiceRunner(notificationService);

        // Should not throw even if email fails
        runner.run();

        verify(notificationService).sendStartupNotification();
    }
}
