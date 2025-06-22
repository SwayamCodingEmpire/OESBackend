package com.cozentus.oes.services;

import com.cozentus.oes.dto.RegisterStudentDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.entities.UserInfo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService {
    List<UserInfoDTO> getAll();
    UserInfoDTO add(UserInfoDTO dto);
    UserInfoDTO updateUserInfo(UserInfoDTO dto);
    void deleteByCode(String email, String code);
    UserInfoDTO registerStudent(RegisterStudentDTO dto);
	void bulkUpload(List<UserInfoDTO> userInfoDTOList);
}
