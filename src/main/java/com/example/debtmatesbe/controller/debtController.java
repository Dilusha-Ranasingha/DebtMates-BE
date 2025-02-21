package com.example.debtmatesbe.controller;


import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.service.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
