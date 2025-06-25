package com.cozentus.oes.services;

import com.cozentus.oes.dto.DateBoundaryDTO;

public interface MiscellaniousService {
	DateBoundaryDTO getMonthBoundary();
	DateBoundaryDTO getWeekBoundary();

}
