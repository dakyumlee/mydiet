package com.mydiet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcome(@RequestParam(required = false) String newUser, Model model) {
        if ("true".equals(newUser)) {
            model.addAttribute("message", "회원가입이 완료되었습니다! MyDiet에 오신 걸 환영합니다! 🎉");
        } else {
            model.addAttribute("message", "다시 오신 걸 환영합니다! 😊");
        }
        return "welcome";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}