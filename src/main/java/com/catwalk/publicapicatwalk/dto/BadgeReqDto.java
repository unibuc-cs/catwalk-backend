package com.catwalk.publicapicatwalk.dto;

import com.catwalk.publicapicatwalk.model.constants.BadgeType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeReqDto implements Serializable {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String image;

    @NotNull
    private BadgeType badgeType;
}
