package com.catwalk.publicapicatwalk.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank(message = "{email.notblank}")
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank(message = "{password.notblank}")
    @Size(min = 6, max = 32, message = "{password.size}")
    private String password;

    @NotBlank(message = "{firstName.notblank}")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "{lastName.notblank}")
    @Size(max = 50)
    private String lastName;

}
