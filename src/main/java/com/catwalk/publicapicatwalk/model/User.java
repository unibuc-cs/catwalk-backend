package com.catwalk.publicapicatwalk.model;

import com.catwalk.publicapicatwalk.model.constants.Sex;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;


@Entity
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends GenericEntity {

    @NotBlank(message = "{firstName.notblank}")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "{lastName.notblank}")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "{email.notblank}")
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank(message = "{password.notblank}")
    @Size(min = 6, message = "{password.size}")
    private String password;

    @NotNull(message = "{sex.notblank}")
    private Sex sex;

    @NotNull(message = "{intaltime.notblank}")
    private Double inaltime; // m

    @NotNull(message = "{greutate.notblank}")
    private Double greutate; // kg

    @NotNull(message = "{varsta.notblank}")
    private Integer varsta; // ani

    private String role;

    private Boolean isEnabled = true;

    private String avatar;

    @JsonManagedReference
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private Set<Exercise> exercises;

    @JsonManagedReference
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private Set<Alimentation> alimentations;

    @JsonManagedReference
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private Set<Badge> badges;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scoreboard_id", referencedColumnName = "id")
    private Scoreboard scoreboard;

    public User(String username, String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getFullname() {
        return this.firstName + " " + this.lastName;
    }

}
