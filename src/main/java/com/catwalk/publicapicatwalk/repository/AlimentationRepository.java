package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Alimentation;
import com.catwalk.publicapicatwalk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlimentationRepository extends JpaRepository<Alimentation, String> {
    List<Alimentation> findAllByUser(User user);
}
