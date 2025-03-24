package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.UserRepository;
import com.example.debtmatesbe.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AdminController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Getter
    @Setter
    public static class UpdateUserRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Role is required")
        private String role;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByRole(User.Role.USER, pageable);
        if (userPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users found");
        }
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/admins")
    public ResponseEntity<?> getAllAdmins(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        if (!username.equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can view all admins");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> adminPage = userRepository.findByRole(User.Role.ADMIN, pageable);
        if (adminPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No admins found");
        }
        return ResponseEntity.ok(adminPage);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRequest updatedUser,
                                        @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        User userToUpdate = userRepository.findById(id).orElse(null);
        if (userToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User existingUser = userRepository.findByUsername(updatedUser.getUsername());
        if (existingUser != null && !existingUser.getId().equals(userToUpdate.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        User existingEmail = userRepository.findByEmail(updatedUser.getEmail());
        if (existingEmail != null && !existingEmail.getId().equals(userToUpdate.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
        }

        userToUpdate.setUsername(updatedUser.getUsername());
        userToUpdate.setEmail(updatedUser.getEmail());
        userToUpdate.setRole(User.Role.valueOf(updatedUser.getRole()));
        User savedUser = userRepository.save(userToUpdate);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        User userToDelete = userRepository.findById(id).orElse(null);

        if (currentUser == null || userToDelete == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        // Prevent deleting the SuperAdmin
        if (userToDelete.getUsername().equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot delete SuperAdmin");
        }

        // Only SuperAdmin can delete other admins
        if (userToDelete.getRole() == User.Role.ADMIN && !currentUser.getUsername().equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can delete other admins");
        }

        userRepository.delete(userToDelete);
        return ResponseEntity.ok("User deleted successfully");
    }
}