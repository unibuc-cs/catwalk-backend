package com.catwalk.publicapicatwalk.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


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
    private String password;

    private String role;

    public User(String username, String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getFullname() {
        return this.firstName + " " + this.lastName;
    }

}
