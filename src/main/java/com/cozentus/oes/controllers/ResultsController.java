package com.cozentus.oes.controllers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.dto.SectionSummaryDTO;
import com.cozentus.oes.dto.SubmitExamRequest;
import com.cozentus.oes.services.ExamDataService;
import com.cozentus.oes.services.ResultsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/exams")
public class ResultsController {

    @Autowired private ExamDataService examQuestionService;
    @Autowired private ResultsService resultsService;

    @GetMapping("/{examCode}/questions")
    public ResponseEntity<List<QuestionBankDTO>> getQuestions(@PathVariable String examCode) {
        return ResponseEntity.ok(examQuestionService.getAllQuestionsOfExam(examCode));
    }

    @PostMapping("/{examCode}/submit")
    public ResponseEntity<Void> submitExam(
        @PathVariable String examCode,
        @Valid @RequestBody SubmitExamRequest request
    ) {
        // now this lines up with your interface + impl
        resultsService.submitExam(examCode, request.answers());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{examCode}/summary")
    public ResponseEntity<List<SectionSummaryDTO>> getSummary(@PathVariable String examCode) {
        var summary = resultsService.getSummaryByExamCode(examCode, null);
        return ResponseEntity.ok(summary);
    }
}
