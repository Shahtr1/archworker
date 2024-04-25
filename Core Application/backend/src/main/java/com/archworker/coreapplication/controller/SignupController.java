package com.archworker.coreapplication.controller;

import com.archworker.coreapplication.dto.ErrorDTO;
import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.service.AuthService;
import com.archworker.coreapplication.util.common.CommonMethods;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/signup")
public class SignupController {

    private final AuthService authService;


    @Autowired
    public SignupController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDTO signupDTO, HttpServletRequest request) {
        boolean isUserCreated = authService.createUser(signupDTO);

        String successMessage = "User created successfully!";
        String failedMessage = "Failed to create user";

        if (isUserCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
        } else {
            ErrorDTO errorDTO = new ErrorDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    failedMessage,
                    CommonMethods.getSingletonListFromKeyAndValue("email", "User email already exists."),
                    request.getRequestURI());

            return ResponseEntity.badRequest().body(errorDTO);
        }
    }
}
