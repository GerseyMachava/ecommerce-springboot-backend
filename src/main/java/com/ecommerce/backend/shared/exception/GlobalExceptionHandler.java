package com.ecommerce.backend.shared.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ecommerce.backend.model.enums.ApiStatus;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> resourceNotFoundHandler(BusinessException exception) {
        return ResponseEntity.status(
                exception.getHttpStatus()).body(
                        ApiResponse.error(exception.getMessage(), exception.getHttpStatus(),
                                exception.getClass().getSimpleName()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("Validation failed", HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> dataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(
                HttpStatus.CONFLICT).body(
                        ApiResponse.error("Resource already registered or integrity violation.", HttpStatus.CONFLICT,
                                ex.getClass().getSimpleName()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR).body(
                        ApiResponse.error("Sorry, something went wrong with our servers.",
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ex.getClass().getSimpleName()));
    }

}
