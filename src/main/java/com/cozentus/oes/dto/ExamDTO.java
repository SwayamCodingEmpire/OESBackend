package com.cozentus.oes.dto;



import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ExamDTO (
	@NotBlank(message = "Code cannot be blank")
    String code,
    	@NotBlank(message = "Name cannot be blank")
    String name,
    @NotNull(message = "Exam date cannot be blank")
    LocalDate examDate,
    @NotNull(message = "Exam Time cannot be blank")
    LocalTime examTime)
{

}

