package com.cozentus.oes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.entities.QuestionBank;
import com.cozentus.oes.entities.Topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Integer> {
	int deleteByCode(String code);
	
	
	@Modifying
	@Transactional
	@Query("UPDATE QUESTION_BANK q SET q.enabled = false WHERE q.code = :code")
	int softDeleteByCode(String code);
	
	List<QuestionBank> findAllByEnabledTrue(Pageable pageable);
	
	@Query("SELECT qb FROM QUESTION_BANK qb LEFT JOIN FETCH qb.topic WHERE qb.enabled = true")
	List<QuestionBank> findAllByEnabledTrueJoinFetchTopic();

	Optional<QuestionBank> findByCode(@NotBlank(message = "Code is required") @Size(max = 50) String code);
	boolean existsByCode(String code);
    List<QuestionBank> findAllByCodeIn(List<String> codes);


}
