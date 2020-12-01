package com.catwalk.publicapicatwalk.controller.web.dto;

import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto implements Serializable {

    private static final long serialVersionUID = -2529480627270604124L;

    @JsonProperty("status")
    private StatusCode status;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("message")
    private String errorMessage;

}
