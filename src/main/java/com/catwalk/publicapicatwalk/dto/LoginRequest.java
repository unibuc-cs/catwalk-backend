package com.catwalk.publicapicatwalk.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "{email.notblank}")
    private String email;

    @NotBlank(message = "{password.notblank}")
    private String password;

}
