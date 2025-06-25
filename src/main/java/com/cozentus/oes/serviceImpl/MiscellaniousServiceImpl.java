package com.cozentus.oes.serviceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.DateBoundaryDTO;
import com.cozentus.oes.services.MiscellaniousService;
@Service
public class MiscellaniousServiceImpl implements MiscellaniousService {
	
	public DateBoundaryDTO getMonthBoundary() {
		LocalDate now = LocalDate.now();
		LocalDate firstDayThisMonth = now.withDayOfMonth(1);
		LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);

		LocalDateTime startOfThisMonth = firstDayThisMonth.atStartOfDay();
		
		LocalDateTime endOfThisMonth = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

		LocalDateTime startOfLastMonth = firstDayLastMonth.atStartOfDay();
		LocalDateTime endOfLastMonth = firstDayLastMonth.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
		return new DateBoundaryDTO(
				startOfThisMonth,
				endOfThisMonth,
				startOfLastMonth,
				endOfLastMonth
				);
	}
	
	public DateBoundaryDTO getWeekBoundary() {
	    LocalDate today = LocalDate.now();

	    // Start and end of this week (Monday to Sunday)
	    LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
	    LocalDate endOfThisWeek = today.with(DayOfWeek.SUNDAY);

	    // Start and end of last week (subtract 1 week, then get Mon–Sun again)
	    LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
	    LocalDate endOfLastWeek = endOfThisWeek.minusWeeks(1);

	    return new DateBoundaryDTO(
	        startOfThisWeek.atStartOfDay(),
	        endOfThisWeek.atTime(23, 59, 59),
	        startOfLastWeek.atStartOfDay(),
	        endOfLastWeek.atTime(23, 59, 59)
	    );
	}


}
