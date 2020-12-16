package com.catwalk.publicapicatwalk.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaReqDto implements Serializable {

    @NotBlank
    private String url;

}
