package com.mydiet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard.html";
    }
    
    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin-login.html";
    }
}