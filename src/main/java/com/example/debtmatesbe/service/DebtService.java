package com.example.debtmatesbe.service;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.DebtRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DebtService {
    @Autowired
    DebtRepo debtRepo;

    public boolean saveUser(User user) {
        return debtRepo.save(user).getId() != null;
    }

    public boolean deleteUserById(Long id) {
        Optional<User> user = debtRepo.findById(id);
        if (user.isPresent()) {
            debtRepo.deleteById(id);
            return true;
        }
        return false;
    }

}
