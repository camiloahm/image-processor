
package com.image.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
class RestResponseExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleBadRequests(
            RuntimeException ex, WebRequest request) {
        final String bodyOfResponse = "Please check your request";
        log.error(ex.getMessage(), ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        final String bodyOfResponse = "Internal server error, please contact the administrator";
        log.error(ex.getMessage(), ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    protected ResponseEntity getInternalError(String error) {
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity getBadRequest(String error) {
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}
