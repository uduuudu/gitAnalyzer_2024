package com.Dima.gitAnalyzer.controller;

import com.Dima.gitAnalyzer.entity.User;
import com.Dima.gitAnalyzer.repository.UserRepository;
import com.Dima.gitAnalyzer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;



    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            model.put("message", "Пользователь уже существует!");
            return "registration";
        }

        System.out.println(user.getUsername() + ' ' + user.getPassword());
        user.setActive(true);
        userService.registerUser(user.getUsername(), user.getPassword());

        return "redirect:/login";
    }
}