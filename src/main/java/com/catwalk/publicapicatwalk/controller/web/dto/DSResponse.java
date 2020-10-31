package com.catwalk.publicapicatwalk.controller.web.dto;

import com.catwalk.publicapicatwalk.controller.web.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class DSResponse<T> {

    private static final long serialVersionUID = -5697095657720264251L;

    @JsonProperty("success")
    private List<T> successResponse;

    @JsonIgnore
    private ErrorCode errorResponse;

    @JsonProperty("error")
    private HashMap<String, String> error = new HashMap<>();

    public DSResponse(List<T> successResponse, ErrorCode errorResponse) {
        this.successResponse = successResponse;
        this.errorResponse = errorResponse;
        error.put("code", errorResponse.getCode());
        error.put("message", errorResponse.getMessage());
    }

    public DSResponse(List<T> successResponse) {
        this.successResponse = successResponse;
    }

    public DSResponse(ErrorCode errorResponse) {
        this.errorResponse = errorResponse;
        error.put("code", errorResponse.getCode());
        error.put("message", errorResponse.getMessage());
    }
}
