package com.cozentus.oes.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.cozentus.oes.dto.ExamAverageDTO;
import com.cozentus.oes.dto.ExamCompletionStatusDTO;
import com.cozentus.oes.dto.ExamScheduleDTO;
import com.cozentus.oes.dto.LeaderBoardPayloadDTO;
import com.cozentus.oes.dto.PresentAndPercentageIncreaseDTO;
import com.cozentus.oes.dto.FieldWisePerformanceDTO;

public interface OverallPlatformSummaryService {
	List<FieldWisePerformanceDTO> fetchTopicWisePerformance(Integer pageNo);

	PresentAndPercentageIncreaseDTO fetchStudentTrends();
	PresentAndPercentageIncreaseDTO fetchWeeklyTotalExams();
	
	PresentAndPercentageIncreaseDTO fetchMonthlyCompletedExams();
	
	PresentAndPercentageIncreaseDTO calculateAveragePercentagePerMonth();
	List<ExamScheduleDTO> fetchExamSchedule(LocalDate start, LocalDate end);
	List<LeaderBoardPayloadDTO> fetchLeaderBoard(String examCode);
	List<FieldWisePerformanceDTO> calculateExamWisePercentage(int pageNo, int pageSize);
	Map<String, Double> getPassFailPercentage();
	 ExamCompletionStatusDTO getExamCompletionStatusDTO();
}
