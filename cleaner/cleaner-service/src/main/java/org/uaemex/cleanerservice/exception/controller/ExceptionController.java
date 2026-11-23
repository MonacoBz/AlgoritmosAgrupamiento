package org.uaemex.cleanerservice.exception.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.uaemex.cleanerservice.exception.ValidationException;
import org.uaemex.cleanerservice.models.dto.GowerExceptionResponseDto;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<GowerExceptionResponseDto> validationError(ValidationException e){
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatusCode.valueOf(422)).body(new GowerExceptionResponseDto(message));
    }


}
