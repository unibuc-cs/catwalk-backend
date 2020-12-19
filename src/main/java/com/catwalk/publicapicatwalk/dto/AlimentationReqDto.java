package com.catwalk.publicapicatwalk.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlimentationReqDto implements Serializable {

    @NotBlank
    private String name;

    @NotNull
    private Integer noCalories;

    @NotNull
    private Integer score;

}
