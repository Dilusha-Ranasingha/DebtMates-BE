package com.example.debtmatesbe.controller;


import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.service.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debt")
public class debtController {

    @Autowired
    DebtService debtService;

    @GetMapping("getDebt")
    public String getDebt(@RequestBody User user) {

        if(debtService.saveUser(user)){
            return "success";
        }
        return "false";

    }

    @DeleteMapping("delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        boolean deleted = debtService.deleteUserById(id);
        return deleted ? "User deleted successfully" : "User not found";
    }

}
