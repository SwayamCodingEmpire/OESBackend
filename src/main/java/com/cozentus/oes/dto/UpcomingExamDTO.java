package com.cozentus.oes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpcomingExamDTO {
    private String examCode;
    private String name;
    private String date;
    private Integer duration;
    private Integer totalMarks;
    private String topic;
}
