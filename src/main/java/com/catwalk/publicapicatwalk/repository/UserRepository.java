package com.catwalk.publicapicatwalk.repository;

import java.util.Optional;

import com.catwalk.publicapicatwalk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
