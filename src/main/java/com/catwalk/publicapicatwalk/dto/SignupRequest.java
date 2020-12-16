package com.catwalk.publicapicatwalk.dto;

import com.catwalk.publicapicatwalk.model.constants.Sex;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "{sex.notblank}")
    private Sex sex;

    @NotNull(message = "{intaltime.notblank}")
    private Double inaltime; // m

    @NotNull(message = "{greutate.notblank}")
    private Double greutate; // kg

    @NotNull(message = "{varsta.notblank}")
    private Integer varsta; // ani

}
