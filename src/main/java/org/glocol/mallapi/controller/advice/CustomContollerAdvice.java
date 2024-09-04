package org.glocol.mallapi.controller.advice;

import java.util.Map;

import org.glocol.mallapi.util.CustomJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class CustomContollerAdvice {
    @ExceptionHandler(CustomJwtException.class)
    protected ResponseEntity<?> handleJWTException(CustomJwtException e) {
        String msg = e.getMessage();
        return ResponseEntity.ok().body(Map.of("error", msg));
    }
}
