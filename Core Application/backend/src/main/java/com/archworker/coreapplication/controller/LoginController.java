package com.archworker.coreapplication.controller;

import com.archworker.coreapplication.dto.ErrorDTO;
import com.archworker.coreapplication.dto.JwtDTO;
import com.archworker.coreapplication.dto.LoginDTO;
import com.archworker.coreapplication.service.jwt.UserServiceImpl;
import com.archworker.coreapplication.util.common.CommonMethods;
import com.archworker.coreapplication.util.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("login")
public class LoginController {
    private final AuthenticationManager authenticationManager;

    private final UserServiceImpl userService;

    private final JwtUtil jwtUtil;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, UserServiceImpl userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(), loginDTO.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            String failedMessage = "Authentication failed. Invalid username or password.";

            ErrorDTO errorDTO = new ErrorDTO(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    failedMessage,
                    CommonMethods.getSingletonListFromKeyAndValue("auth", "Invalid credentials provided."),
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
        }

        UserDetails userDetails;

        try {
            userDetails = userService.loadUserByUsername(loginDTO.getEmail());
        } catch (UsernameNotFoundException e) {
            String failedMessage = "Authentication failed. User not found.";

            ErrorDTO errorDTO = new ErrorDTO(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    failedMessage,
                    CommonMethods.getSingletonListFromKeyAndValue("auth", "User not found for provided email."),
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
        }

        String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtDTO(jwt));
    }
}
