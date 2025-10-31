package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailServiceRunner implements CommandLineRunner {

    private final NotificationService notificationService;

    @Override
    public void run(String... args) {
        notificationService.sendStartupNotification();
    }
}
