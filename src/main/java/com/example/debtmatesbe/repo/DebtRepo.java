package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepo extends JpaRepository<User, Long> {

}
