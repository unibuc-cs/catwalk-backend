package com.catwalk.publicapicatwalk.controller.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ErrorCode {
    CREATE_FAILURE("GENERAL_CREATION_ERROR", "Creation failure", 500),
    UPDATE_FAILURE("GENERAL_UPDATE_ERROR", "Update failure", 500),
    PATCH_FAILURE("GENERAL_PATCH_ERROR", "Patch failure", 500),
    DELETE_FAILURE("GENERAL_DELETE_ERROR", "Delete failure", 500),

    GENERAL_UNEXPECTED_ERROR("GENERAL_UNEXPECTED_EXCEPTION", "Unexpected exception", 500),
    GENERAL_VALIDATION_ERROR("GENERAL_VALIDATION_ERROR", "General validation error", 500),
    GENERAL_NOT_FOUND_ERROR("GENERAL_NOT_FOUND_ERROR", "Not found entity exception", 404);

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonIgnore
    private Integer status;

    ErrorCode(String code, String message, Integer status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
