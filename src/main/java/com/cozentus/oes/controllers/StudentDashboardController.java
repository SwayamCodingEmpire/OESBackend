package com.cozentus.oes.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.LeaderboardEntryDTO;
import com.cozentus.oes.dto.StudentDashboardDTO;
import com.cozentus.oes.services.ResultsService;
import com.cozentus.oes.services.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;
    private final ResultsService resultsService;

    @GetMapping
    public ResponseEntity<StudentDashboardDTO> getDashboard() {
        Integer studentId = studentDashboardService.getCurrentStudentId(); // get from JWT/session
        StudentDashboardDTO dashboard = studentDashboardService.getDashboardForStudent(studentId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/leaderboard/{examCode}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(@PathVariable String examCode) {
        List<LeaderboardEntryDTO> leaderboard = resultsService.getLeaderboardForExam(examCode);
        return ResponseEntity.ok(leaderboard);
    }
}
