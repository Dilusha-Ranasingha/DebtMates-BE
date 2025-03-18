package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.UserRepository;
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
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Getter
    @Setter
    public static class UpdateProfileRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;
    }

    @Getter
    @Setter
    public static class PasswordChangeRequest {
        @NotBlank(message = "Old password is required")
        private String oldPassword;

        @NotBlank(message = "New password is required")
        private String newPassword;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        String actualToken = extractToken(token);
        String username = jwtUtil.extractUsername(actualToken);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody UpdateProfileRequest updatedUser) {
        String actualToken = extractToken(token);
        String username = jwtUtil.extractUsername(actualToken);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User existingUser = userRepository.findByUsername(updatedUser.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token,
                                                 @Valid @RequestBody PasswordChangeRequest request) {
        String actualToken = extractToken(token);
        String username = jwtUtil.extractUsername(actualToken);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password updated successfully");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(@RequestHeader("Authorization") String token) {
        String actualToken = extractToken(token);
        String username = jwtUtil.extractUsername(actualToken);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userRepository.delete(user);
        return ResponseEntity.ok("Profile deleted successfully");
    }

    private String extractToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return token.substring(7);
    }
}