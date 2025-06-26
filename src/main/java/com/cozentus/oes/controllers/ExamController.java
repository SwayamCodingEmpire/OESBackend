	package com.cozentus.oes.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.services.ExamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping
    public ResponseEntity<ExamDTO> createExam(@Valid @RequestBody ExamDTO dto) {
        return ResponseEntity.ok(examService.createExam(dto));
    }

    @PutMapping("/{code}")
    public ResponseEntity<ExamDTO> updateExam(@PathVariable String code, @RequestBody ExamDTO dto) {
        return ResponseEntity.ok(examService.updateExam(code, dto));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ExamDTO> getExamByCode(@PathVariable String code) {
        return ResponseEntity.ok(examService.getExamByID(code));
    }

    @GetMapping("/all")
    public List<ExamDTO> getAllExams() {
        return examService.getAllExams();
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteExam(@PathVariable String code) {
        examService.deleteExam(code);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/codes")
    public ResponseEntity<List<String>> getAllExamCodes() {
		return ResponseEntity.ok(examService.getAllExamCodes());
	}
    
}
