package com.cozentus.oes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.CodeAndNameDTO;
import com.cozentus.oes.dto.ExamQuestionkInsertionDTO;
import com.cozentus.oes.dto.IdAndCodeDTO;
import com.cozentus.oes.entities.Topic;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
	// Additional query methods can be defined here if needed
	Optional<Topic> findByCode(String code);
	boolean existsByCode(String code);
	
	List<CodeAndNameDTO> findAllByEnabledTrueOrderByNameAsc();
	
	List<Topic> findAllByEnabledTrue();
	
	@Modifying
	@Transactional
	int deleteByCode(String code);
	
	List<IdAndCodeDTO> findAllByCodeInAndEnabledTrue(List<String> codes);
	
	
//	@Query("SELECT COUNT(t) FROM Topic t WHERE t.code IN :codes")
//	long countByCodeIn(@Param("codes") List<String> codes);
}
