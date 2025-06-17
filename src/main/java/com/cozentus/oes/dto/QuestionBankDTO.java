package com.cozentus.oes.dto;

import java.util.List;

import com.cozentus.oes.entities.QuestionBank;
import com.cozentus.oes.helpers.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record QuestionBankDTO(

	    @NotBlank(message = "Code is required")
	    @Size(max = 50)
	    String code,

	    @NotBlank(message = "Question text is required")
	    String question,
	    
	    @Size(min = 4, max = 4, message = "Exactly 4 options are required")
	    List<String> options,
	    

	    @Pattern(regexp = "[A-D]", message = "Correct option must be A, B, C, or D")
	    String correctOption,

	    
	    String comments,

	    @NotNull(message = "Topic ID is required")
	    String topicCode,

	    Boolean enabled,

	    @NotNull(message = "Difficulty is required")
	    Difficulty difficulty,
	    Integer marks
	) {
	
	public QuestionBankDTO(QuestionBank questionBank) {
		this(
			questionBank.getCode(),
			questionBank.getQuestion(),
			List.of(questionBank.getOptionA(), questionBank.getOptionB(), questionBank.getOptionC(), questionBank.getOptionD()),
			questionBank.getCorrectOption().toString(),
			questionBank.getComments(),
			questionBank.getTopic().getCode(),
			questionBank.getEnabled(),
			questionBank.getDifficulty(),
			questionBank.getMarks()
		);
	}
	
}
