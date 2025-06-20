package com.cozentus.oes.repositories;

import java.util.List;

// src/main/java/com/cozentus/oes/repositories/ExamSectionRepository.java

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamSection;

public interface ExamSectionRepository extends JpaRepository<ExamSection, Integer> {
    @Query("SELECT es FROM ExamSection es WHERE es.exam = :exam AND es.section.code = :sectionCode")
    Optional<ExamSection> findByExamAndSectionCode(@Param("exam") Exam exam, @Param("sectionCode") String code);
    List<ExamSection> findByExam_Code(String examCode);

}
