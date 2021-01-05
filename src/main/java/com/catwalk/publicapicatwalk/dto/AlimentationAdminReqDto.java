package com.catwalk.publicapicatwalk.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlimentationAdminReqDto implements Serializable {

    private String name;

    private Integer noCalories;

    private Integer score;

}
