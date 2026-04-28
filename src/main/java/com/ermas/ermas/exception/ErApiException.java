package com.ermas.ermas.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErApiException extends RuntimeException {

    private final HttpStatus status;

    public ErApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
