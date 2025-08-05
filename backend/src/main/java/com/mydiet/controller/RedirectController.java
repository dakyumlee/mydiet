package com.mydiet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "redirect:/auth.html";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard.html";
    }
    
    @GetMapping("/welcome")
    public String welcome() {
        return "redirect:/dashboard.html";
    }
    
    @GetMapping("/success")
    public String success() {
        return "redirect:/dashboard.html";
    }
    
    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin-login.html";
    }
    
    @GetMapping("/settings")
    public String settings() {
        return "redirect:/profile-settings.html";
    }
    
    @GetMapping("/meals")
    public String meals() {
        return "redirect:/meal-management.html";
    }
    
    @GetMapping("/workouts")
    public String workouts() {
        return "redirect:/workout-management.html";
    }
    
    @GetMapping("/emotions")
    public String emotions() {
        return "redirect:/emotion-diary.html";
    }
    
    @GetMapping("/analytics")
    public String analytics() {
        return "redirect:/analytics.html";
    }
}