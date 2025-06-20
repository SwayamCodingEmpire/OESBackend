package com.cozentus.oes.repositories;
import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamStudent;

import com.cozentus.oes.entities.UserInfo;

public interface ExamStudentRepository extends JpaRepository<ExamStudent, Integer>  {
	Optional<ExamStudent> findByExamAndStudent(Exam exam, UserInfo student);
	
	List<ExamStudent> findByExam(Exam exam);
	
	void deleteByExamIdAndStudentId(Integer examId, Integer studentId);
	Optional<ExamStudent> findByExamAndStudentIsNull(Exam exam);
}

