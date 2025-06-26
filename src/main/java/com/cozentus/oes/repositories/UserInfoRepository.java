package com.cozentus.oes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.entities.UserInfo;
import com.cozentus.oes.helpers.Roles;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByCode(String code);
    void deleteByCode(String code);
    boolean existsByCode(String code);
    
    List<UserInfo> findAllByCredentialsRole(Roles role);
    @Cacheable(value = "userInfo", key = "#username")
	Optional<UserInfo> findByEmailAndEnabledTrue(String username);
	
	List<UserInfo> findAllByCodeInAndEnabledTrue(List<String> codes);
	Optional<UserInfo> findByEmail(String username);

	

}
