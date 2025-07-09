package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Page<User> findByRole(User.Role role, Pageable pageable);

    // Add method to find user by email
    User findByEmail(String email);

    // Add methods for searching by username and email (case-insensitive)
    List<User> findByUsernameContainingIgnoreCase(String username);

    List<User> findByEmailContainingIgnoreCase(String email);
}