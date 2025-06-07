package com.app.playbooker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.playbooker.utils.AppConstants.ERROR_MSG;
import static com.app.playbooker.utils.AppConstants.ERROR_STATUS;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        List<String> err = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            err.add(errorMessage);
        });
        errors.put("errors", err);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, Object>> handleUserException(UserException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.BAD_REQUEST.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PlaySpaceException.class)
    public ResponseEntity<Map<String, Object>> handlePlaySpaceException(PlaySpaceException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.BAD_REQUEST.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PlaySpaceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlaySpaceNotFoundException(PlaySpaceNotFoundException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.NOT_FOUND.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, Object>> handleBookingException(BookingException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.BAD_REQUEST.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBookingNotFoundException(BookingNotFoundException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.NOT_FOUND.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OtpException.class)
    public ResponseEntity<Map<String, Object>> handleOtpException(OtpException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.BAD_REQUEST.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<Map<String, Object>> handleReviewException(ReviewException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.BAD_REQUEST.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.FORBIDDEN.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.FORBIDDEN.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInvoiceNotFoundException(InvoiceNotFoundException ex) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put(ERROR_STATUS, HttpStatus.NOT_FOUND.value());
        errorObject.put(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }
}
