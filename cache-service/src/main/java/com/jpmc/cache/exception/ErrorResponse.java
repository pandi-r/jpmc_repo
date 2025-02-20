/**
 * 
 */
package com.jpmc.cache.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the structure of an error response sent to the client when an exception occurs.
 * The response contains details about the error, including the timestamp of when the error occurred,
 * the HTTP status code, a brief error description, and the specific message associated with the error.
 * 
 * This class is used in various exception handlers to provide consistent error responses across the application.
 * 
 * @author r.pandiarajan
 *
 */
@Getter
@AllArgsConstructor
public class ErrorResponse
{
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

}
