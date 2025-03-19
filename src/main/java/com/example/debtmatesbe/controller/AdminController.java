package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.UserRepository;
import com.example.debtmatesbe.util.JwtUtil;
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
        Pageable pageable = PageRequest.of(page, size);
        Page<User> adminPage = userRepository.findByRole(User.Role.ADMIN, pageable);
        if (adminPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No admins found");
        }
        return ResponseEntity.ok(adminPage);
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

        if (currentUser.getUsername().equals("SuperAdmin")) {
            // SuperAdmin can delete any user, including other admins
            userRepository.delete(userToDelete);
            return ResponseEntity.ok("User deleted successfully");
        } else if (currentUser.getRole() == User.Role.ADMIN) {
            // Other admins can only delete non-admin users
            if (userToDelete.getRole() == User.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only SuperAdmin can delete other admins");
            }
            userRepository.delete(userToDelete);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
    }
}