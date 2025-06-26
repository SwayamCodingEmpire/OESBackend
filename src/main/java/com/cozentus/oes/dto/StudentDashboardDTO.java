package com.cozentus.oes.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDashboardDTO {
    private UserInfoDTO studentInfo;
    private List<ExamStatsDTO> attemptedExams;
    private List<TopicStatsDTO> topicStats;
    private List<DifficultyStatsDTO> difficultyStats;
    private List<UpcomingExamDTO> upcomingExams;
    private Integer classRank;
    private Integer totalStudents;
}
