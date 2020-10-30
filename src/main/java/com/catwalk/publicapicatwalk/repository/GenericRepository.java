package com.catwalk.publicapicatwalk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericRepository<T> extends JpaRepository<T, String> {
}
