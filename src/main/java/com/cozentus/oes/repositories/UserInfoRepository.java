package com.cozentus.oes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.oes.dto.RegisterStudentDTO;
import com.cozentus.oes.entities.UserInfo;
import com.cozentus.oes.helpers.Roles;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByCode(String code);
    void deleteByCode(String code);
    boolean existsByCode(String code);
    
    List<UserInfo> findAllByCredentialsRole(Roles role);

}
