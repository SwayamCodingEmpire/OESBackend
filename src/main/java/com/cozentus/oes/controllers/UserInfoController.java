package com.cozentus.oes.controllers;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cozentus.oes.dto.RegisterStudentDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.services.UserInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/Student")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

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
    public ResponseEntity<Void> delete(@PathVariable String code) {
        userInfoService.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }
}
