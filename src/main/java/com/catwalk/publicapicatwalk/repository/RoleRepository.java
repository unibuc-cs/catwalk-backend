package com.catwalk.publicapicatwalk.repository;

import java.util.Optional;

import com.catwalk.publicapicatwalk.model.ERole;
import com.catwalk.publicapicatwalk.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
