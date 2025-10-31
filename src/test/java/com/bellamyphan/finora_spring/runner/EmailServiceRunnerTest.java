package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.service.NotificationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailServiceRunnerTest {

    @Mock
    private NotificationService notificationService;

    // Todo: Temporary disable this test
//    @Test
//    void run_shouldCallSendStartupNotification() throws Exception {
//        // Arrange
//        EmailServiceRunner runner = new EmailServiceRunner(notificationService);
//
//        // Act
//        runner.run();
//
//        // Assert
//        verify(notificationService).sendStartupNotification();
//    }
}
