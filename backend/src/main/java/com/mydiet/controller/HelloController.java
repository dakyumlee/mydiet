package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class HelloController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/")
    public String hello() {
        return "MyDiet App is running! 🎯";
    }
    
    @GetMapping("/api/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/api/health")
    public String health() {
        long userCount = userRepository.count();
        return "DB Connected! Users: " + userCount;
    }
}
