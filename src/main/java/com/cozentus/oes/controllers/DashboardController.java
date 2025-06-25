package com.cozentus.oes.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.ExamCompletionStatusDTO;
import com.cozentus.oes.dto.ExamScheduleDTO;
import com.cozentus.oes.dto.FieldWisePerformanceDTO;
import com.cozentus.oes.dto.LeaderBoardPayloadDTO;
import com.cozentus.oes.dto.PresentAndPercentageIncreaseDTO;
import com.cozentus.oes.services.OverallPlatformSummaryService;

@RestController
@RequestMapping("/v1/admin/dashboard")
public class DashboardController {

	private final OverallPlatformSummaryService overallPlatformSummaryService;

	public DashboardController(OverallPlatformSummaryService overallPlatformSummaryService) {
		this.overallPlatformSummaryService = overallPlatformSummaryService;
	}

	@GetMapping("/topic-wise-performance")
	public ResponseEntity<List<FieldWisePerformanceDTO>> getTopicWisePerformance(
			@RequestParam("page") Integer topicPageNo) {
		return ResponseEntity.ok(overallPlatformSummaryService.fetchTopicWisePerformance(topicPageNo));
	}

	@GetMapping("/student-enrollment")
	public ResponseEntity<PresentAndPercentageIncreaseDTO> getStudentEnrollment() {
		return ResponseEntity.ok(overallPlatformSummaryService.fetchStudentTrends());
	}

	@GetMapping("/weekly-exam-count")
	public ResponseEntity<PresentAndPercentageIncreaseDTO> getWeeklyExamCount() {
		return ResponseEntity.ok(overallPlatformSummaryService.fetchWeeklyTotalExams());
	}

	@GetMapping("/monthly-exam-completed-count")
	public ResponseEntity<PresentAndPercentageIncreaseDTO> getMonthlyCompletedExams() {
		return ResponseEntity.ok(overallPlatformSummaryService.fetchMonthlyCompletedExams());
	}

	@GetMapping("/monthly-average-result")
	public ResponseEntity<PresentAndPercentageIncreaseDTO> getMonthlyAverageResult() {
		return ResponseEntity.ok(overallPlatformSummaryService.calculateAveragePercentagePerMonth());
	}

	@GetMapping("/exam-schedule")
	public ResponseEntity<List<ExamScheduleDTO>> getExamSchedule(@RequestParam int year, @RequestParam int month) {
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
		return ResponseEntity.ok(overallPlatformSummaryService.fetchExamSchedule(start, end));
	}
	
	@GetMapping("/leaderboard/{examCode}")
	public ResponseEntity<List<LeaderBoardPayloadDTO>> getLeaderboard(@PathVariable String examCode) {
		return ResponseEntity.ok(overallPlatformSummaryService.fetchLeaderBoard(examCode));
	}
	
	@GetMapping("/exam-wise-percentage")
	public ResponseEntity<List<FieldWisePerformanceDTO>> getExamWisePercentage(
			@RequestParam("examPageNo") Integer examPageNo) {
		return ResponseEntity.ok(overallPlatformSummaryService.calculateExamWisePercentage(examPageNo,10));
	}
	
	@GetMapping("/pass-fail-percentage")
	public ResponseEntity<Map<String, Double>> getPassFailPercentage() {
		return ResponseEntity.ok(overallPlatformSummaryService.getPassFailPercentage());
	}
	
	@GetMapping("/exam-completion-status")
	public ResponseEntity<ExamCompletionStatusDTO> getExamCompletionStatus() {
		return ResponseEntity.ok(overallPlatformSummaryService.getExamCompletionStatusDTO());
	}
	

}
