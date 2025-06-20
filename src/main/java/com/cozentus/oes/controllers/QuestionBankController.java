package com.cozentus.oes.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
		 URI location = ServletUriComponentsBuilder
			        .fromCurrentRequest()
			        .path("/{code}")
			        .buildAndExpand(questionBankDTO.code())
			        .toUri();
		 return ResponseEntity.created(location).body("Question added successfully");
	}
	
	@GetMapping("/{code}")
	public ResponseEntity<QuestionBankDTO> getQuestionById(@PathVariable String code) {
		return ResponseEntity.ok(questionBankService.getQuestionById(code));
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<List<QuestionBankDTO>> getAllQuestionsPageable(@RequestParam(defaultValue = "0") int page, 
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "code") String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		return ResponseEntity.ok(questionBankService.getAllQuestionsPageable(pageable));// Placeholder for actual implementation
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<QuestionBankDTO>> getAllQuestions() {
		return ResponseEntity.ok(questionBankService.getAllQuestions());// Placeholder for actual implementation
	}
	
	
	
	@DeleteMapping("/{code}")
	public ResponseEntity<Void> deleteQuestion(@PathVariable String code) {
	    questionBankService.deleteQuestion(code);
	    return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{code}")
	public ResponseEntity<String> updateQuestion(@RequestBody @Valid QuestionBankDTO questionBankDTO, @PathVariable String code) {
		questionBankService.updateQuestion(questionBankDTO, code);
		return ResponseEntity.ok("Question udpated successfully");
	}
	
	@PostMapping("/{topicCode}/bulk-upload")
	public ResponseEntity<String> bulkUploadQuestions(@RequestBody List<QuestionBankDTO> questionBankDTOs, @PathVariable String topicCode) {
		if (questionBankDTOs.isEmpty()) {
			return ResponseEntity.badRequest().body("No questions provided for bulk upload");
		};
		
		questionBankService.bulkInsertQuestions(questionBankDTOs, topicCode);
		
		return ResponseEntity.ok("Bulk upload successful");
	}
	
}
