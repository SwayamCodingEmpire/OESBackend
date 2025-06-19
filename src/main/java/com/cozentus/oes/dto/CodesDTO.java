package com.cozentus.oes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CodesDTO (
		List<String> codes
		)
{
	
}

