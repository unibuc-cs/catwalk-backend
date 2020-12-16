package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {
}
