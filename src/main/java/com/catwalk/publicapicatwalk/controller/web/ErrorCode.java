package com.catwalk.publicapicatwalk.controller.web;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    CREATE_FAILURE("GENERAL_CREATION_ERROR", "Creation failure", StatusCode.FAIL, HttpStatus.BAD_REQUEST),
    UPDATE_FAILURE("GENERAL_UPDATE_ERROR", "Update failure", StatusCode.FAIL, HttpStatus.BAD_REQUEST),
    PATCH_FAILURE("GENERAL_PATCH_ERROR", "Patch failure", StatusCode.FAIL, HttpStatus.BAD_REQUEST),
    DELETE_FAILURE("GENERAL_DELETE_ERROR", "Delete failure", StatusCode.FAIL, HttpStatus.BAD_REQUEST),

    GENERAL_UNEXPECTED_ERROR("GENERAL_UNEXPECTED_EXCEPTION", "Unexpected exception", StatusCode.FAIL, HttpStatus.BAD_REQUEST),
    GENERAL_VALIDATION_ERROR("GENERAL_VALIDATION_ERROR", "General validation error", StatusCode.FAIL, HttpStatus.BAD_REQUEST),
    GENERAL_NOT_FOUND_ERROR("GENERAL_NOT_FOUND_ERROR", "Not found entity exception", StatusCode.ERROR, HttpStatus.NOT_FOUND),

    // User
    EMAIL_NOT_UNIQUE("EMAIL_NOT_UNIQUE", "Email is already in use!", StatusCode.ERROR, HttpStatus.BAD_REQUEST),
    WRONG_CREDENTIALS("WRONG_CREDENTIALS", "The email and password provided are incorrect!", StatusCode.ERROR, HttpStatus.BAD_REQUEST);

    private String code;

    private StatusCode status;

    private String message;

    private HttpStatus httpStatusCode;

    ErrorCode(String code, String message, StatusCode status, HttpStatus httpStatusCode) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.httpStatusCode = httpStatusCode;
    }
}
