package com.cozentus.oes.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.entities.Topic;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
	// Additional query methods can be defined here if needed
	Optional<Topic> findByCode(String code);
}
