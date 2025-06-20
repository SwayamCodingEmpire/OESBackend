package com.cozentus.oes.dto;

import com.cozentus.oes.helpers.Roles;

public record LoginResponseDTO(
		String token,
		Roles role
		) {

}
