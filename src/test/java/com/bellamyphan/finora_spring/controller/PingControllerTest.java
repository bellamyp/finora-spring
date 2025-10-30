package com.bellamyphan.finora_spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PingControllerTest {

    @InjectMocks
    private PingController pingController;

    @BeforeEach
    void resetRateLimiter() throws Exception {
        // Reset the requestCount and windowStart before each test
        Field requestCountField = PingController.class.getDeclaredField("requestCount");
        requestCountField.setAccessible(true);
        AtomicInteger requestCount = (AtomicInteger) requestCountField.get(pingController);
        requestCount.set(0);

        Field windowStartField = PingController.class.getDeclaredField("windowStart");
        windowStartField.setAccessible(true);
        windowStartField.set(pingController, Instant.now());
    }

    @Test
    void ping_shouldReturnPong_whenUnderLimit() {
        // Act
        String response = pingController.ping();

        // Assert
        assertEquals("pong", response);
    }

    @Test
    void ping_shouldReturnRateLimitExceeded_whenOverLimit() throws Exception {
        // Access private fields to set the counter close to the limit
        Field requestCountField = PingController.class.getDeclaredField("requestCount");
        requestCountField.setAccessible(true);
        AtomicInteger requestCount = (AtomicInteger) requestCountField.get(pingController);

        // Set the counter to MAX_REQUESTS_PER_MINUTE
        Field maxField = PingController.class.getDeclaredField("MAX_REQUESTS_PER_MINUTE");
        maxField.setAccessible(true);
        int maxRequests = (int) maxField.get(null);

        requestCount.set(maxRequests);

        // Act
        String response = pingController.ping();

        // Assert
        assertEquals("Rate limit exceeded. Try again later.", response);
    }
}
