package com.cozentus.oes.repositories;

import com.cozentus.oes.entities.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Optional<Exam> findByCode(String code);
    void deleteByCode(String code);
}

