package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.ActivityLog;
import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.ActivityLogRepository;
import com.example.debtmatesbe.repo.UserRepository;
import com.example.debtmatesbe.service.EmailService;
import com.example.debtmatesbe.service.OtpService;
import com.example.debtmatesbe.service.TokenService;
import com.example.debtmatesbe.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final OtpService otpService;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, ActivityLogRepository activityLogRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenService tokenService,
                          OtpService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @Getter
    @Setter
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;
    }

    @Getter
    @Setter
    public static class LoginRequest {
        @NotBlank(message = "Username or email is required")
        private String username; // This will now be used for both username and email

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Getter
    @Setter
    public static class PasswordResetRequest {
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;
    }

    @Getter
    @Setter
    public static class PasswordResetConfirmRequest {
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "OTP is required")
        private String otp;

        @NotBlank(message = "New password is required")
        private String newPassword;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request,
                                           @RequestHeader(value = "Authorization", required = false) String token) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User.Role role = User.Role.USER;
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            String adminUsername = jwtUtil.extractUsername(actualToken);
            User admin = userRepository.findByUsername(adminUsername);
            if (admin != null && admin.getRole() == User.Role.ADMIN && admin.getUsername().equals("SuperAdmin")) {
                role = User.Role.ADMIN;
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can register admins");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(role);
        userRepository.save(user);

        // Log registration activity
        ActivityLog activityLog = new ActivityLog(ActivityLog.ActivityType.REGISTRATION,
                user.getUsername(), user.getEmail(), user.getRole().toString());
        activityLogRepository.save(activityLog);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // Check if the provided input matches either username or email
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            user = userRepository.findByEmail(request.getUsername());
        }

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username/email or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().toString());
        tokenService.storeToken(token);

        // Log login activity for admins only
        if (user.getRole() == User.Role.ADMIN) {
            ActivityLog activityLog = new ActivityLog(ActivityLog.ActivityType.LOGIN,
                    user.getUsername(), user.getEmail(), user.getRole().toString());
            activityLogRepository.save(activityLog);
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
        String actualToken = token.substring(7);
        tokenService.invalidateToken(actualToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }

        // Generate OTP and send it via email
        String otp = otpService.generateOtp(request.getEmail());
        try {
            emailService.sendOtpEmail(request.getEmail(), otp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP email: " + e.getMessage());
        }

        return ResponseEntity.ok("OTP sent to your email. Please check your inbox.");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        // Verify the OTP
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }

        // Find the user and update the password
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Clear the OTP
        otpService.clearOtp(request.getEmail());

        return ResponseEntity.ok("Password reset successfully");
    }
}