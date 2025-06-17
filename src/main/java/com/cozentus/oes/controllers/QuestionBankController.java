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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.DeleteExchange;

import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.services.QuestionBankService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/question-bank")
public class QuestionBankController {
	private final QuestionBankService questionBankService;
	
	public QuestionBankController(QuestionBankService questionBankService) {
		this.questionBankService = questionBankService;
	}
	
	@PostMapping
	public ResponseEntity<String> addQuestion(@RequestBody @Valid QuestionBankDTO questionBankDTO) {
		questionBankService.addQuestion(questionBankDTO);
		return ResponseEntity.ok("Question added successfully");
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<QuestionBankDTO>> getAllQuestions() {
		return ResponseEntity.ok(questionBankService.getAllQuestions());// Placeholder for actual implementation
	}
	
	
	@DeleteMapping("/{code}")
	public void deleteQuestion(@PathVariable String code) {
		questionBankService.deleteQuestion(code);
		// Placeholder for actual implementation
		// return ResponseEntity.ok("Question deleted successfully");
	}
	
	@PutMapping
	public ResponseEntity<String> updateQuestion(@RequestBody @Valid QuestionBankDTO questionBankDTO) {
		questionBankService.updateQuestion(questionBankDTO);
		return ResponseEntity.ok("Question udpated successfully");
	}
	
}
