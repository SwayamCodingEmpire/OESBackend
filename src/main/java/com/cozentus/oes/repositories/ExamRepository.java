package com.cozentus.oes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.entities.Exam;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
	// Additional query methods can be defined here if needed
	Exam findByCode(String code);
	
	
}
