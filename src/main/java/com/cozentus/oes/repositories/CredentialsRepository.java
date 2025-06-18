package com.cozentus.oes.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.entities.Credentials;

public interface CredentialsRepository extends JpaRepository<Credentials, Integer> {
    boolean existsByEmail(String email);
    Optional<Credentials> findByCode(String code);
	void deleteByCode(String code);
}
