package com.atharva.nutricheckai.controller;

import com.atharva.nutricheckai.dto.LoginRequest;
import com.atharva.nutricheckai.dto.LoginResponse;
import com.atharva.nutricheckai.dto.RegisterResponse;
import com.atharva.nutricheckai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.atharva.nutricheckai.dto.RegisterRequest;
import com.atharva.nutricheckai.entity.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid  @RequestBody RegisterRequest request){

        return userService.registerUser(request);

    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        return userService.loginUser(request);

    }


}

