package com.ermas.ermas.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErApiException.class)
    public ResponseEntity<Map<String, Object>> handleErApiException(ErApiException e) {
        return ResponseEntity.status(e.getStatus())
                .body(Map.of(
                        "status", e.getStatus().value(),
                        "error", e.getStatus().getReasonPhrase(),
                        "message", e.getMessage()
                ));
    }
}
