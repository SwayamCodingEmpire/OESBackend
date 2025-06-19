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

import com.cozentus.oes.dto.CodesDTO;
import com.cozentus.oes.dto.ExamSectionDTO;
import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.services.ExamDataService;
import com.cozentus.oes.services.ExamService;
import com.cozentus.oes.util.EmailService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/exam")
public class ExamDataController {

    private final EmailService emailService;

    @Autowired
    private ExamDataService examDataService;
    
    ExamDataController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    private ExamService examService;

    @PostMapping("/{examCode}/question")
    public ResponseEntity<String> addQuestionsToExam(
            @PathVariable String examCode,
            @RequestBody CodesDTO codesDTO) {

        examDataService.addQuestionsToExam(examCode, codesDTO);
        return ResponseEntity.ok("Questions added to exam.");
    }

    @DeleteMapping("/{examCode}/question/{questionCode}")
    public ResponseEntity<String> removeQuestionFromExam(
            @PathVariable String examCode,
            @PathVariable String questionCode) {

        examDataService.removeQuestionFromExam(examCode, questionCode);
        return ResponseEntity.ok("Question removed from exam.");
    }

    @GetMapping("/{examCode}/questions/all")
    public ResponseEntity<List<QuestionBankDTO>> getAllQuestionsOfExam(
            @PathVariable String examCode) {

        List<QuestionBankDTO> questions = examDataService.getAllQuestionsOfExam(examCode);
        return ResponseEntity.ok(questions);
    }
    
    @PostMapping("/{code}/sections")
    public ResponseEntity<String> addSectionToExam(
			@PathVariable String code,
			@Valid @RequestBody List<ExamSectionDTO> examSectionDTO) {

    	examService.addExamSection(examSectionDTO, code);
		return ResponseEntity.ok("Section added to exam.");
	}
    
    @PostMapping("/{code}/students")
    public ResponseEntity<String> addStudentToExam(
            @PathVariable String code,
            @RequestBody CodesDTO studentCodesDTO) {

    	examDataService.addStudentsToExam(code, studentCodesDTO);
		return ResponseEntity.ok("Section added to exam.");
	}
    
    @GetMapping("/{examCode}/students")
    public ResponseEntity<List<UserInfoDTO>> getAllStudentsOfExam(
			@PathVariable String examCode) {

		List<UserInfoDTO> students = examDataService.getAllStudentsOfExam(examCode);
		return ResponseEntity.ok(students);
	}
    
    
    @PostMapping("/{examCode}/instant-exam")
    public ResponseEntity<String> addInstantExam(
			@PathVariable String examCode,
			@RequestBody List<QuestionBankDTO> questionBankDTOs) {

		examDataService.addInstantExam(examCode, questionBankDTOs);
		return ResponseEntity.ok("Instant exam created successfully.");
	}
    
    @DeleteMapping("/{examCode}/students/{studentCode}")
    public ResponseEntity<String> deleteStudentFromExam(
			@PathVariable String examCode, @PathVariable String studentCode) {

		examDataService.deleteStudentFromExam(examCode, studentCode);
		return ResponseEntity.noContent().build();
	}

}
