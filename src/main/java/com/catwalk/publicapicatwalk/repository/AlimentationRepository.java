package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Alimentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlimentationRepository extends JpaRepository<Alimentation, String> {
}
