package com.catwalk.publicapicatwalk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "alimentations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alimentation extends GenericEntity {

    @NotBlank
    private String name;

    @NotNull
    private Integer noCalories;

    @NotNull
    private Integer score;

    @JsonManagedReference
    @OneToMany(mappedBy="alimentation", cascade = CascadeType.ALL)
    private Set<Media> media;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
