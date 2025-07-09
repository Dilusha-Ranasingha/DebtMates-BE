package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.ActivityLog;
import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.ActivityLogRepository;
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
    private final ActivityLogRepository activityLogRepository;
    private final JwtUtil jwtUtil;

    public AdminController(UserRepository userRepository, ActivityLogRepository activityLogRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.jwtUtil = jwtUtil;
    }

    @Getter
    @Setter
    public static class UpdateAdminRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;
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

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(user);
    }

    // Endpoint for admins to view user registration activity
    @GetMapping("/activity/registrations/users")
    public ResponseEntity<?> getUserRegistrationActivity(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> activityPage = activityLogRepository.findByActivityTypeAndRole(
                ActivityLog.ActivityType.REGISTRATION, "USER", pageable);
        if (activityPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No user registration activity found");
        }
        return ResponseEntity.ok(activityPage);
    }

    // Endpoint for SuperAdmin to view all registration activity (users and admins)
    @GetMapping("/activity/registrations/all")
    public ResponseEntity<?> getAllRegistrationActivity(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        if (!username.equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can view all registration activity");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> activityPage = activityLogRepository.findByActivityType(
                ActivityLog.ActivityType.REGISTRATION, pageable);
        if (activityPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No registration activity found");
        }
        return ResponseEntity.ok(activityPage);
    }

    // Endpoint for SuperAdmin to view admin login activity
    @GetMapping("/activity/logins/admins")
    public ResponseEntity<?> getAdminLoginActivity(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        if (!username.equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can view admin login activity");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> activityPage = activityLogRepository.findByActivityTypeAndRole(
                ActivityLog.ActivityType.LOGIN, "ADMIN", pageable);
        if (activityPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No admin login activity found");
        }
        return ResponseEntity.ok(activityPage);
    }

    // Endpoint for SuperAdmin to edit an admin
    @PutMapping("/admins/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long id,
                                         @Valid @RequestBody UpdateAdminRequest updatedAdmin,
                                         @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        if (!username.equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can edit admins");
        }

        User adminToUpdate = userRepository.findById(id).orElse(null);
        if (adminToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }
        if (adminToUpdate.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not an admin");
        }

        User existingUser = userRepository.findByUsername(updatedAdmin.getUsername());
        if (existingUser != null && !existingUser.getId().equals(adminToUpdate.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        User existingEmail = userRepository.findByEmail(updatedAdmin.getEmail());
        if (existingEmail != null && !existingEmail.getId().equals(adminToUpdate.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
        }

        adminToUpdate.setUsername(updatedAdmin.getUsername());
        adminToUpdate.setEmail(updatedAdmin.getEmail());
        User savedAdmin = userRepository.save(adminToUpdate);
        return ResponseEntity.ok(savedAdmin);
    }

    // Endpoint for SuperAdmin to delete an admin
    @DeleteMapping("/admins/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);
        User currentUser = userRepository.findByUsername(username);
        User adminToDelete = userRepository.findById(id).orElse(null);

        if (currentUser == null || adminToDelete == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }

        if (currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        if (!currentUser.getUsername().equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can delete admins");
        }
        if (adminToDelete.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not an admin");
        }
        if (adminToDelete.getUsername().equals("SuperAdmin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot delete SuperAdmin");
        }

        userRepository.delete(adminToDelete);
        return ResponseEntity.ok("Admin deleted successfully");
    }
}