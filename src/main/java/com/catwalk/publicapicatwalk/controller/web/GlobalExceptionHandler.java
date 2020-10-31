package com.catwalk.publicapicatwalk.controller.web;

import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.controller.web.dto.DSResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.catwalk.publicapicatwalk.controller.web.ErrorCode.GENERAL_VALIDATION_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                       HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new DSResponse<>(GENERAL_VALIDATION_ERROR), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity handleGenericException(GenericException e) {
        return new ResponseEntity<>(new DSResponse(e.getErrorCode()), HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

}
