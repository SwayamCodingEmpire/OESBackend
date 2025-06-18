package com.cozentus.oes.dto;

import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;

@Data
public class ExamQuestionRequestDTO {
	@NotEmpty(message = "Exam code cannot be empty")
    private List<String> questionCodes;
}

