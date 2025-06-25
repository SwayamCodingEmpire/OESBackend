package com.cozentus.oes.dto;

import java.time.LocalDate;

public record StudentExamPercentageDTO(
	    Integer studentId,
	    Integer examId,
	    LocalDate examDate,
	    double totalScored,
	    double totalPossible,
	    double percentage
	) {}

