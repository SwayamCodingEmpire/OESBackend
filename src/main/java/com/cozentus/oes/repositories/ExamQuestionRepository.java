package com.cozentus.oes.repositories;

import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamQuestion;
import com.cozentus.oes.entities.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Integer> {
    List<ExamQuestion> findByExam(Exam exam);
    Optional<ExamQuestion> findByExamAndQuestion(Exam exam, QuestionBank question);
    void deleteByExamAndQuestion(Exam exam, QuestionBank question);
}
