/**
 * 
 */
package com.jpmc.cache.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

/**
 * Global exception handler for handling various exceptions in the application.
 * This class uses {@link RestControllerAdvice} to handle exceptions across all 
 * controllers in the application. It provides custom error responses for 
 * different types of exceptions.
 * 
 * - {@link EntityNotFoundException} is handled by returning a 404 Not Found response with a custom error message.
 * - A general {@link Exception} is handled by returning a 500 Internal Server Error response for unexpected errors.
 * 
 * @author r.pandiarajan
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    /**
     * Handles {@link EntityNotFoundException} by returning a 404 Not Found response 
     * with a custom error message.
     * 
     * @param ex The {@link EntityNotFoundException} that was thrown.
     * @return A {@link ResponseEntity} containing the {@link ErrorResponse} with status 404.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), 
                "Resource Not found", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles any general {@link Exception} by returning a 500 Internal Server Error response 
     * with a generic error message.
     * 
     * @param ex The {@link Exception} that was thrown.
     * @return A {@link ResponseEntity} containing the {@link ErrorResponse} with status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "An Error Occured", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
