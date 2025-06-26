package com.cozentus.oes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamStatsDTO {
    private String examCode;
    private String examName;
    private String date;
    private String topic;
    private Integer score;
    private Integer maxScore;
    private Integer timeTaken; // seconds
    private Integer rank;
    private DifficultyBreakdownDTO difficultyBreakdown;
}
