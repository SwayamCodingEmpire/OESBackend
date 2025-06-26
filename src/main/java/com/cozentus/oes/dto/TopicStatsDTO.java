package com.cozentus.oes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicStatsDTO {
    private String topic;
    private Double averageScore;
}
