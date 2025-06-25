package com.cozentus.oes.dto;

import java.time.LocalDateTime;

public record DateBoundaryDTO(
		 LocalDateTime startOfThis,
	    LocalDateTime endOfThis,
	    LocalDateTime startOfLast,
	    LocalDateTime endOfLast
		) {

}
