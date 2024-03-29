package com.archworker.coreapplication.exception;


import com.archworker.coreapplication.dto.ErrorDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final String validationErrorName = "Validation Error";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        ErrorDTO errorDTO = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                validationErrorName,
                errors,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorDTO errorDTO = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                validationErrorName,
                errors,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

}
