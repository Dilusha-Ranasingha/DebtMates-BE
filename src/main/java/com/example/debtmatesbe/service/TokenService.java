package com.example.debtmatesbe.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenService {

    private final Set<String> validTokens = new HashSet<>();

    public void storeToken(String token) {
        validTokens.add(token);
    }

    public void invalidateToken(String token) {
        validTokens.remove(token);
    }

    public boolean isTokenValid(String token) {
        return validTokens.contains(token);
    }
}