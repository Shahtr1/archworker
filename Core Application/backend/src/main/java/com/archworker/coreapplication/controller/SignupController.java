package com.archworker.coreapplication.controller;

import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
public class SignupController {

    private final AuthService authService;


    @Autowired
    public SignupController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<String> signup(@Valid @RequestBody SignupDTO signupDTO) {
        boolean isUserCreated = authService.createUser(signupDTO);

        if (isUserCreated) {
            String successMessage = "User created successfully!";
            return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
        } else {
            String failedMessage = "Failed to create user";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failedMessage);
        }
    }
}
