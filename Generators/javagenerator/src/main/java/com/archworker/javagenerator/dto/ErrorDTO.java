package com.archworker.javagenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ErrorDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<String> messages;
    private String path;

}
