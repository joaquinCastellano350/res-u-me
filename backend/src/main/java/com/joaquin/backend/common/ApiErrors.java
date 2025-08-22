package com.joaquin.backend.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String,Object>> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "not_found",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "conflict",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString()
        ));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "bad_request",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString()
        ));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error","validation_error",
                "message","invalid_input",
                "details", ex.getBindingResult().getFieldErrors().stream()
                        .collect(java.util.stream.Collectors.toMap(f->f.getField(), f->f.getDefaultMessage(), (a,b)->a)),
                "timestamp", Instant.now().toString()
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> generic(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error","internal","message","unexpected_error","timestamp", Instant.now().toString()
        ));
    }
}
