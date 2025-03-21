package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Page<User> findByRole(User.Role role, Pageable pageable);

    // Add method to find user by email
    User findByEmail(String email);
}