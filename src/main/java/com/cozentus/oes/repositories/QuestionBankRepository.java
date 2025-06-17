package com.cozentus.oes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.entities.QuestionBank;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Integer> {
	int deleteByCode(String code);
	
	@Modifying
	@Transactional
	@Query("UPDATE QUESTION_BANK q SET q.enabled = false WHERE q.code = :code")
	int softDeleteByCode(String code);
	
	List<QuestionBank> findAllByEnabledTrue();

}
