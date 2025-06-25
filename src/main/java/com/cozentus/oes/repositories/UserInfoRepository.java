package com.cozentus.oes.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cozentus.oes.dto.DatePeriodWiseCountDTO;
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
	
	
	@Query(""+
			  "SELECT new com.cozentus.oes.dto.DatePeriodWiseCountDTO( " + 
			    "SUM(CASE WHEN e.createdAt BETWEEN :startOfThisMonth AND :endOfThisMonth THEN 1 ELSE 0 END), " +
			    "SUM(CASE WHEN e.createdAt BETWEEN :startOfLastMonth AND :endOfLastMonth THEN 1 ELSE 0 END) " +
			  ") " +
			  "FROM UserInfo e WHERE e.enabled=true " +
			"")
	DatePeriodWiseCountDTO findPresentAndPercentageIncreaseOfTotalStudentsMonthly(
			@Param("startOfThisMonth") LocalDateTime startOfThisMonth,
		    @Param("endOfThisMonth") LocalDateTime endOfThisMonth,
		    @Param("startOfLastMonth") LocalDateTime startOfLastMonth,
		    @Param("endOfLastMonth") LocalDateTime endOfLastMonth);

	

}
