package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Scoreboard;
import com.catwalk.publicapicatwalk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, String> {
    Optional<Scoreboard> findByUser(User user);

    @Query(value = "SELECT * FROM clasament ORDER BY alimentation_score DESC", nativeQuery = true)
    List<Scoreboard> findAllOrderByAlimentationScoreDesc();

    @Query(value = "SELECT * FROM clasament ORDER BY exercise_score DESC", nativeQuery = true)
    List<Scoreboard> findAllOrderByExerciseScoreDesc();

    @Query(value = "SELECT * FROM clasament ORDER BY total_score DESC", nativeQuery = true)
    List<Scoreboard> findAllOrderByTotalScoreDesc();
}
