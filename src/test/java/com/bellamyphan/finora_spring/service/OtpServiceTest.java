package com.bellamyphan.finora_spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
    }

    // ------------------ generateOtp ------------------
    @Test
    void testGenerateOtpLength() {
        String otp = otpService.generateOtp();
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"), "OTP should be 6 digits");
    }

    @Test
    void testGenerateOtpMultipleDifferent() {
        String otp1 = otpService.generateOtp();
        String otp2 = otpService.generateOtp();
        assertNotEquals(otp1, otp2, "Two generated OTPs should usually be different");
    }

    // ------------------ save/get/clear OTP ------------------
    @Test
    void testSaveAndGetOtp() {
        String email = "test@example.com";
        String otp = "123456";
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        otpService.saveOtp(email, otp, expiry);

        OtpService.OtpEntry entry = otpService.getOtp(email);
        assertNotNull(entry);
        assertEquals(otp, entry.otp());
        assertEquals(expiry, entry.expiry());
    }

    @Test
    void testGetOtpIsCaseInsensitive() {
        String email = "Test@Example.com";
        String otp = "654321";
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        otpService.saveOtp(email, otp, expiry);

        OtpService.OtpEntry entry = otpService.getOtp(email.toLowerCase());
        assertNotNull(entry);
        assertEquals(otp, entry.otp());
    }

    @Test
    void testClearOtp() {
        String email = "clear@example.com";
        String otp = "000000";
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        otpService.saveOtp(email, otp, expiry);
        assertNotNull(otpService.getOtp(email));

        otpService.clearOtp(email);
        assertNull(otpService.getOtp(email));
    }

    // ------------------ cleanupExpiredOtps ------------------
    @Test
    void testCleanupExpiredOtps() {
        String email1 = "expired@example.com";
        String email2 = "valid@example.com";

        otpService.saveOtp(email1, "111111", LocalDateTime.now().minusMinutes(1)); // expired
        otpService.saveOtp(email2, "222222", LocalDateTime.now().plusMinutes(5));  // valid

        otpService.cleanupExpiredOtps();

        assertNull(otpService.getOtp(email1), "Expired OTP should be removed");
        assertNotNull(otpService.getOtp(email2), "Valid OTP should remain");
    }
}
