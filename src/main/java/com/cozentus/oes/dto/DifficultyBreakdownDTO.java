package com.cozentus.oes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DifficultyBreakdownDTO {
    private Integer easy;
    private Integer medium;
    private Integer hard;
}
