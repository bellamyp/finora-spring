package com.bellamyphan.finora_spring.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final SecureRandom random = new SecureRandom();

    // In-memory OTP storage
    private final Map<String, OtpEntry> otpMap = new ConcurrentHashMap<>();

    /**
     * Generate a default 6-digit numeric OTP
     * @return OTP as String
     */
    public String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10)); // 0-9
        }
        return otp.toString();
    }

    /** Save OTP for email with expiry */
    public void saveOtp(String email, String otp, LocalDateTime expiry) {
        otpMap.put(email.toLowerCase(), new OtpEntry(otp, expiry));
    }

    /** Retrieve OTP entry for verification */
    public OtpEntry getOtp(String email) {
        return otpMap.get(email.toLowerCase());
    }

    /** Clear OTP after verification or expiry */
    public void clearOtp(String email) {
        otpMap.remove(email.toLowerCase());
    }

    @Scheduled(fixedRate = 3_600_000) // run every 1 hour
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpMap.entrySet().removeIf(entry -> entry.getValue().expiry().isBefore(now));
    }

    /**
     * OTP entry class
     */
        public record OtpEntry(String otp, LocalDateTime expiry) {
    }
}
