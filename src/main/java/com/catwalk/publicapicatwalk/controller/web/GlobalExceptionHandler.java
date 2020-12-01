package com.catwalk.publicapicatwalk.controller.web;

import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                       HttpStatus status, WebRequest request) {
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.ERROR)
                .errorMessage("There was a problem processing your request. Please try again later.")
                .build();
        return new ResponseEntity<ResponseDto>(oResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity handleGenericException(GenericException e) {
        ResponseDto oResponse = ResponseDto.builder()
                .status(e.getErrorCode().getStatus())
                .errorMessage(e.getErrorCode().getMessage())
                .build();
        return new ResponseEntity<ResponseDto>(oResponse, e.getErrorCode().getHttpStatusCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleGenericException(AuthenticationException e) {
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.ERROR)
                .errorMessage(ErrorCode.WRONG_CREDENTIALS.getMessage())
                .build();
        return new ResponseEntity<ResponseDto>(oResponse, ErrorCode.WRONG_CREDENTIALS.getHttpStatusCode());
    }

}
