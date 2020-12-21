package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Scoreboard;
import com.catwalk.publicapicatwalk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, String> {
    Optional<Scoreboard> findByUser(User user);
    List<Scoreboard> findAllOrderByAlimentationScoreDesc();
    List<Scoreboard> findAllOrderByExerciseScoreDesc();
    List<Scoreboard> findAllOrderByTotalScoreDesc();
}
