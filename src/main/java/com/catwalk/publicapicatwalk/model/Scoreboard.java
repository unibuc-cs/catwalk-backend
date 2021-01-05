package com.catwalk.publicapicatwalk.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "clasament")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scoreboard extends GenericEntity {

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @NotNull
    private Integer alimentationScore;

    @NotNull
    private Integer exerciseScore;

    @NotNull
    private Integer totalScore;

    public void updateTotalScore() {
        this.totalScore = this.exerciseScore + this.alimentationScore;
    }

    public void increaseEx(Integer value) {
        this.exerciseScore += value;
        updateTotalScore();
    }

    public void decreaseEx(Integer value) {
        this.exerciseScore -= value;
        updateTotalScore();
    }

    public void increaseAlim(Integer value) {
        this.alimentationScore += value;
        updateTotalScore();
    }

    public void decreaseAlim(Integer value) {
        this.alimentationScore -= value;
        updateTotalScore();
    }

}
