package com.archworker.angulargenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("hello")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping
    public String hello(@RequestHeader("arch-correlation-id") String correlationId){

        logger.debug("arch-correlation-id: {}", correlationId);
        return "hello world!";
    }
}
