package com.example.debtmatesbe.service;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.DebtRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DebtService {
    @Autowired
    DebtRepo debtRepo;

    public boolean saveUser(User user) {
        return debtRepo.save(user).getId() != null;
    }

}
