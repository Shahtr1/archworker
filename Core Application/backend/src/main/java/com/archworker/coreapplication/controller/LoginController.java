package com.archworker.coreapplication.controller;

import com.archworker.coreapplication.dto.JwtDTO;
import com.archworker.coreapplication.dto.LoginDTO;
import com.archworker.coreapplication.service.jwt.UserServiceImpl;
import com.archworker.coreapplication.util.JwtUtil;
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
    public ResponseEntity<JwtDTO> login(@RequestBody LoginDTO loginDTO){
        System.out.println(loginDTO);
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),loginDTO.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails;

        try{
            userDetails = userService.loadUserByUsername(loginDTO.getEmail());
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new JwtDTO(jwt));
    }
}
