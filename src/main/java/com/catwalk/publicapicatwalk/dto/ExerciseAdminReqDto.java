package com.catwalk.publicapicatwalk.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseAdminReqDto {
    private String name;
    private Integer minutes;
    private Integer noExercises;
    private Integer score;
}
