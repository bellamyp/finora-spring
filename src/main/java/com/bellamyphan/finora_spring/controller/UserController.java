package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // base path for all user endpoints
@CrossOrigin(origins = {
        "http://localhost:4200",                // for local dev
        "https://finora-angular.vercel.app"    // for Vercel deployment
})
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // GET /api/users - fetch all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}