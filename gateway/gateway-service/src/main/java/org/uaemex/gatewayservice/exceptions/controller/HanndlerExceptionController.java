package org.uaemex.gatewayservice.exceptions.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.uaemex.gatewayservice.exceptions.ValidationException;

@RestControllerAdvice
public class HanndlerExceptionController {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> validationException(ValidationException e){
        return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
    }
}

record ErrorMessage(
        String message
){
}