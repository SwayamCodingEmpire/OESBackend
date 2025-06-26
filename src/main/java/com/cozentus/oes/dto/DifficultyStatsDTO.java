package com.cozentus.oes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DifficultyStatsDTO {
    private String difficulty;
    private Double average;
}
