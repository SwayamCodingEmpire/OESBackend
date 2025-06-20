package com.cozentus.oes.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.SectionDetailDTO;
import com.cozentus.oes.services.ExamSectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/exams")
@RequiredArgsConstructor
public class ExamSectionController {
  
  private final ExamSectionService sectionService;

  @GetMapping("/{examCode}/sections")
  public ResponseEntity<List<SectionDetailDTO>> getAllSectionDetails(
      @PathVariable String examCode) {
    return ResponseEntity.ok(
      sectionService.getSectionsByExamCode(examCode)
    );
  }
}
