package com.nisum.errorhandling;

import com.nisum.errorhandling.model.ErrorBody;
import com.nisum.exception.BadRequestException;
import com.nisum.exception.EmailExistException;
import com.nisum.exception.InvalidEmailException;
import com.nisum.exception.UserNotExistException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ UserNotExistException.class })
    public ResponseEntity<ErrorBody> handleUserNotExistException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                ErrorBody.builder().mensaje("User not exist").build(),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ EmailExistException.class })
    public ResponseEntity<ErrorBody> handleEmailExistException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                ErrorBody.builder().mensaje("Email exist").build(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ InvalidEmailException.class })
    public ResponseEntity<ErrorBody> handleInvalidEmailException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                ErrorBody.builder().mensaje("Email is invalid. It must be in the form <name>@<domain>").build(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ BadRequestException.class })
    public ResponseEntity<ErrorBody> handleBadRequestException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                ErrorBody.builder().mensaje(ex.getCause().getMessage()).build(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }


}
