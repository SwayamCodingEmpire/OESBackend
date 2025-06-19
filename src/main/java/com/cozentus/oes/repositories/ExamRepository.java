package com.cozentus.oes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.entities.Exam;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Optional<Exam> findByCode(String code);
    void deleteByCode(String code);
    
    @Modifying
    @Transactional
    @Query("UPDATE EXAM e SET e.enabled = false WHERE e.code = :code")
    int softDeleteByCode(@Param("code") String code);
    
    List<Exam> findAllByEnabledTrue();
    
}

