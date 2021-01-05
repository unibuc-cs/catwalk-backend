package com.catwalk.publicapicatwalk.model;

import com.catwalk.publicapicatwalk.model.constants.BadgeType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "badges")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badge extends GenericEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String image;

    @NotNull
    private BadgeType badgeType; // BADGE sau AVATAR_REWARD

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
