package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Alimentation;
import com.catwalk.publicapicatwalk.model.Exercise;
import com.catwalk.publicapicatwalk.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {
    List<Media> findByAlimentation(Alimentation a);
    List<Media> findByExercise(Exercise e);
}
