package com.cozentus.oes.controllers;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.dto.RegisterStudentDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.services.AuthenticationService;
import com.cozentus.oes.services.ExamDataService;
import com.cozentus.oes.services.UserInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/student")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final ExamDataService examDataService;
    private final AuthenticationService authenticationService;

    @GetMapping("/all")
    public ResponseEntity<List<UserInfoDTO>> getAll() {
        return ResponseEntity.ok(userInfoService.getAll());
    }

    @PostMapping
    public ResponseEntity<UserInfoDTO> registerStudent(@RequestBody RegisterStudentDTO dto) {
        UserInfoDTO created = userInfoService.registerStudent(dto);
        return ResponseEntity.ok(created);
    }
    
    @PostMapping("/bulk-upload")
    public ResponseEntity<?> bulkUpload(@RequestBody List<UserInfoDTO> userInfoDTOList) {
		userInfoService.bulkUpload(userInfoDTOList);
		return ResponseEntity.ok("Bulk upload successful");
    }

    @PutMapping
    public ResponseEntity<UserInfoDTO> update(@RequestBody UserInfoDTO dto) {
        return ResponseEntity.ok(userInfoService.updateUserInfo(dto));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code, @RequestHeader("email") String email) {
        userInfoService.deleteByCode(email, code);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/exam")
    public ResponseEntity<List<ExamDTO>> getAllExamsByStudent() {
    	Integer id = authenticationService.getCurrentUserDetails().getLeft();
		return ResponseEntity.ok(examDataService.getAllExamsByStudent(id));
	}

}
