package com.example.debtmatesbe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken(String username, String token, long expiration) {
        redisTemplate.opsForValue().set("jwt:" + username, token, expiration, TimeUnit.MILLISECONDS);
    }

    public String getToken(String username) {
        return redisTemplate.opsForValue().get("jwt:" + username);
    }

    public void deleteToken(String username) {
        redisTemplate.delete("jwt:" + username);
    }

    public boolean isTokenValid(String username, String token) {
        String storedToken = getToken(username);
        return storedToken != null && storedToken.equals(token);
    }
}