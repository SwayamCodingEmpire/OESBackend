package com.cozentus.oes.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.entities.Results;

public interface ResultsRepository extends JpaRepository<Results, Integer> {
    List<Results> findByExamStudent_Exam_CodeAndExamStudent_Student_Id(String examCode, Integer studentId);
}
