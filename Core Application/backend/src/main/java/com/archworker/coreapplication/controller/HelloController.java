package com.archworker.coreapplication.controller;

import com.archworker.coreapplication.dto.HelloDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public HelloDTO hello(){
        return new HelloDTO("Hello from Authorized API request.");
    }
}
