package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.UserRepository;
import com.example.debtmatesbe.service.TokenService;
import com.example.debtmatesbe.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final Long expiration;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil, TokenService tokenService, @Value("${jwt.expiration}") Long expiration) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.expiration = expiration;
        logger.info("AuthController initialized with expiration: {}", expiration);
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

        private String role; // Add role field for admin registration
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        // Determine role based on token (admin can register other admins)
        if (token != null && !token.isEmpty()) {
            String actualToken = token.substring(7);
            String username = jwtUtil.extractUsername(actualToken);
            User currentUser = userRepository.findByUsername(username);
            if (currentUser != null && currentUser.getRole() == User.Role.ADMIN) {
                user.setRole(User.Role.ADMIN); // Admin can register other admins
            } else {
                user.setRole(User.Role.USER); // Default to USER for non-admin registration
            }
        } else {
            user.setRole(User.Role.USER); // Default to USER if no token
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("Login attempt for username: {}", user.getUsername());
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            logger.error("Invalid request body: username or password is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
        }

        User foundUser = userRepository.findByUsername(user.getUsername());
        logger.info("Found user: {}", foundUser);
        if (foundUser == null) {
            logger.error("User not found: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        if (passwordEncoder == null) {
            logger.error("PasswordEncoder is null");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server configuration error");
        }

        if (!passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            logger.error("Password mismatch for user: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        if (jwtUtil == null) {
            logger.error("JwtUtil is null");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server configuration error");
        }

        String token = jwtUtil.generateToken(foundUser.getUsername(), foundUser.getRole().toString());
        logger.info("Generated token for user: {}", foundUser.getUsername());
        tokenService.storeToken(foundUser.getUsername(), token, expiration);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        tokenService.deleteToken(username);
        return ResponseEntity.ok("Logged out successfully");
    }
}