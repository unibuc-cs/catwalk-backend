package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Exercise;
import com.catwalk.publicapicatwalk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    List<Exercise> findAllByUser(User user);
}
