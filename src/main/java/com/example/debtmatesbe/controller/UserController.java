package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.UserRepository;
import com.example.debtmatesbe.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
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
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User existingUser = userRepository.findByEmail(updatedUser.getEmail());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
        }

        user.setEmail(updatedUser.getEmail());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token,
                                                 @Valid @RequestBody ChangePasswordRequest request) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Verify the current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email) {
        try {
            // Authenticate the user
            String currentUsername;
            try {
                currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                if (currentUsername == null || currentUsername.equals("anonymousUser")) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed: " + e.getMessage());
            }

            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found in database");
            }

            // Validate at least one search parameter is provided
            if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either username or email must be provided");
            }

            // Search users
            List<User> users;
            if (username != null && !username.isEmpty()) {
                users = userRepository.findByUsernameContainingIgnoreCase(username);
            } else {
                users = userRepository.findByEmailContainingIgnoreCase(email);
            }

            // Exclude the current user from search results
            users = users.stream()
                    .filter(user -> !user.getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());

            // Map to DTO
            List<UserDTO> userDTOs = users.stream()
                    .map(this::mapToUserDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userDTOs);
        } catch (ResponseStatusException e) {
            throw e; // Re-throw ResponseStatusException to return proper status code
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to search users: " + e.getMessage(), e);
        }
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

    // DTO class to control what data is sent to the frontend
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }



    @Getter
    @Setter
    public static class UpdateProfileRequest {
        @NotBlank(message = "Email is required")
        private String email;
    }

    @Getter
    @Setter
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        private String newPassword;
    }
}