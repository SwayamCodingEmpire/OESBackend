package com.cozentus.oes.dto;

public record ExamCompletionStatusDTO(
		Long completed,
		Long inProgress,
		long notStarted) {

}
