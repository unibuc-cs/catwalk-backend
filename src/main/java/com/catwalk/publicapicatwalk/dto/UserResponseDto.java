package com.catwalk.publicapicatwalk.dto;

import com.catwalk.publicapicatwalk.model.constants.Sex;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto implements Serializable {

    private String firstName;

    private String lastName;

    private String email;

    private Sex sex;

    private Double inaltime;

    private Double greutate;

    private Integer varsta;

    private String avatar;

}
