package com.example.debtmatesbe.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {

    private final Map<String, OtpData> otpStore = new HashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds

    // Data class to store OTP and its metadata
    private static class OtpData {
        private final String otp;
        private final long expirationTime;

        public OtpData(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    // Generate a 6-digit OTP
    public String generateOtp(String email) {
        String otp = String.format("%06d", secureRandom.nextInt(999999));
        long expirationTime = System.currentTimeMillis() + OTP_VALIDITY_DURATION;
        otpStore.put(email, new OtpData(otp, expirationTime));
        return otp;
    }

    // Verify the OTP for a given email
    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpStore.get(email);
        if (otpData == null || otpData.isExpired() || !otpData.getOtp().equals(otp)) {
            return false;
        }
        // OTP is valid, remove it after verification
        otpStore.remove(email);
        return true;
    }

    // Clear OTP for a given email
    public void clearOtp(String email) {
        otpStore.remove(email);
    }
}