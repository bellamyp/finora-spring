package com.bellamyphan.finora_spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/ping")
@CrossOrigin(origins = "*") // allow all origins
public class PingController {

    private static final Logger logger = LoggerFactory.getLogger(PingController.class);

    // Simple in-memory rate limiter
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private Instant windowStart = Instant.now();

    @GetMapping
    public synchronized String ping() {
        Instant now = Instant.now();

        // Reset the counter if a minute has passed
        if (now.isAfter(windowStart.plusSeconds(60))) {
            requestCount.set(0);
            windowStart = now;
        }

        // Increment the counter
        int currentCount = requestCount.incrementAndGet();

        if (currentCount > MAX_REQUESTS_PER_MINUTE) {
            logger.warn("Rate limit exceeded for ping endpoint");
            return "Rate limit exceeded. Try again later.";
        }

        logger.info("Ping received from client. Count: {}", currentCount);
        return "pong";
    }
}
