package com.archworker.coreapplication.controller;

import com.archworker.coreapplication.dto.ErrorDTO;
import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.service.AuthService;
import com.archworker.coreapplication.util.common.CommonMethods;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/signup")
public class SignupController {

    private final AuthService authService;
    private final Timer responseTimer;
    private final Counter signUpFailedCounter;

    @Autowired
    public SignupController(AuthService authService, MeterRegistry meterRegistry) {
        this.authService = authService;
        this.responseTimer = meterRegistry.timer("signup.response.time");
        this.signUpFailedCounter = meterRegistry.counter("signup.failed.count");

    }


    @Timed
    @PostMapping
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDTO signupDTO, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        try {

            boolean isUserCreated = authService.createUser(signupDTO);

            String successMessage = "User created successfully!";
            String failedMessage = "Failed to create user";

            if (isUserCreated) {
                return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
            } else {
                if (signUpFailedCounter != null)
                    signUpFailedCounter.increment();

                ErrorDTO errorDTO = new ErrorDTO(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        failedMessage,
                        CommonMethods.getSingletonListFromKeyAndValue("email", "User email already exists."),
                        request.getRequestURI());

                return ResponseEntity.badRequest().body(errorDTO);
            }
        } finally {
            if (responseTimer != null)
                responseTimer.record(System.currentTimeMillis() - startTime,
                        TimeUnit.MILLISECONDS);
        }
    }
}
