package com.example.tracingtest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CounterRepository extends JpaRepository<Counter, Long> {
    @Query("SELECT COALESCE(MAX(c.id), 0) FROM Counter c")
    long findMaxId();
}
