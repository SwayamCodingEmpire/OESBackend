package com.cozentus.oes.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamStudent;

public interface ExamStudentRepository extends JpaRepository<ExamStudent, Integer> {
    Optional<ExamStudent> findByExamAndStudentIsNull(Exam exam);

}
