package com.cozentus.oes.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.ExamQuestionRequestDTO;
import com.cozentus.oes.dto.ExamSectionDTO;
import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.services.ExamQuestionService;
import com.cozentus.oes.services.ExamService;
import com.cozentus.oes.util.EmailService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/exam")
public class ExamQuestionController {

    private final EmailService emailService;

    @Autowired
    private ExamQuestionService examQuestionService;
    
    ExamQuestionController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    private ExamService examService;

    @PostMapping("/{examCode}/question")
    public ResponseEntity<String> addQuestionsToExam(
            @PathVariable String examCode,
            @Valid @RequestBody ExamQuestionRequestDTO requestDTO) {

        examQuestionService.addQuestionsToExam(examCode, requestDTO);
        return ResponseEntity.ok("Questions added to exam.");
    }

    @DeleteMapping("/{examCode}/question/{questionCode}")
    public ResponseEntity<String> removeQuestionFromExam(
            @PathVariable String examCode,
            @PathVariable String questionCode) {

        examQuestionService.removeQuestionFromExam(examCode, questionCode);
        return ResponseEntity.ok("Question removed from exam.");
    }

    @GetMapping("/{examCode}/questions/all")
    public ResponseEntity<List<QuestionBankDTO>> getAllQuestionsOfExam(
            @PathVariable String examCode) {

        List<QuestionBankDTO> questions = examQuestionService.getAllQuestionsOfExam(examCode);
        return ResponseEntity.ok(questions);
    }
    
    @PostMapping("/{code}/sections")
    public ResponseEntity<String> addSectionToExam(
			@PathVariable String code,
			@Valid @RequestBody List<ExamSectionDTO> examSectionDTO) {

    	examService.addExamSection(examSectionDTO, code);
		return ResponseEntity.ok("Section added to exam.");
	}
}
