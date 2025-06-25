package com.cozentus.oes.serviceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.DateBoundaryDTO;
import com.cozentus.oes.dto.DatePeriodWiseCountDTO;
import com.cozentus.oes.dto.ExamCompletionStatusDTO;
import com.cozentus.oes.dto.ExamScheduleDTO;
import com.cozentus.oes.dto.FieldWisePerformanceDTO;
import com.cozentus.oes.dto.LeaderBoardPayloadDTO;
import com.cozentus.oes.dto.LeaderboardDTO;
import com.cozentus.oes.dto.PresentAndPercentageIncreaseDTO;
import com.cozentus.oes.dto.StudentExamPercentageDTO;
import com.cozentus.oes.repositories.ExamRepository;
import com.cozentus.oes.repositories.ExamStudentRepository;
import com.cozentus.oes.repositories.ResultsRepository;
import com.cozentus.oes.repositories.UserInfoRepository;
import com.cozentus.oes.services.MiscellaniousService;
import com.cozentus.oes.services.OverallPlatformSummaryService;
@Service
public class OverallPlatformSummaryServiceImpl implements OverallPlatformSummaryService {
	private final Logger logger = LoggerFactory.getLogger(OverallPlatformSummaryServiceImpl.class);
	private final MiscellaniousService miscellaneousService;
	private final ResultsRepository resultsRepository;
	private final UserInfoRepository userInfoRepository;
	private final ExamRepository examRepository;
	private final ExamStudentRepository examStudentRepository;
	
	
	public OverallPlatformSummaryServiceImpl(ResultsRepository resultsRepository, MiscellaniousService miscellaneousService, UserInfoRepository userInfoRepository, ExamRepository examRepository, ExamStudentRepository examStudentRepository) {
		this.resultsRepository = resultsRepository;
		this.miscellaneousService = miscellaneousService;
		this.userInfoRepository = userInfoRepository;
		this.examRepository = examRepository;
		this.examStudentRepository = examStudentRepository;
	}

	@Override
	public List<FieldWisePerformanceDTO> fetchTopicWisePerformance(Integer topicPageNo) {
		// TODO Auto-generated method stub
		return resultsRepository.calculateTopicWisePerformance(PageRequest.of(topicPageNo, 10));
	}
	
	@Override
	public PresentAndPercentageIncreaseDTO fetchStudentTrends() {
	    DateBoundaryDTO monthBoundary = miscellaneousService.getMonthBoundary();

	    DatePeriodWiseCountDTO monthlyStudentCount = userInfoRepository.findPresentAndPercentageIncreaseOfTotalStudentsMonthly(
	        monthBoundary.startOfThis(),
	        monthBoundary.endOfThis(),
	        monthBoundary.startOfLast(),
	        monthBoundary.endOfLast()
	    );

	    Long thisMonth = monthlyStudentCount.createdThisMonth();
	    Long lastMonth = monthlyStudentCount.createdLastMonth();
	    logger.info("This Month: {}, Last Month: {}", thisMonth, lastMonth);

	    double percentageChange;

	    if (lastMonth == 0) {
	        percentageChange = thisMonth > 0 ? 100.0 : 0.0;
	    } 
	    else {
	        percentageChange = ((thisMonth - lastMonth) / (double) lastMonth) * 100;
	    }

	    return new PresentAndPercentageIncreaseDTO(thisMonth, percentageChange);
	}
	
	
	@Override
	public PresentAndPercentageIncreaseDTO fetchWeeklyTotalExams() {
	    DateBoundaryDTO weekBoundary = miscellaneousService.getWeekBoundary();

	    DatePeriodWiseCountDTO weeklyExamCount = examRepository.findThisWeeklyExamCount(
	        weekBoundary.startOfThis().toLocalDate(),
	        weekBoundary.endOfThis().toLocalDate(),
	        weekBoundary.startOfLast().toLocalDate(),
	        weekBoundary.endOfLast().toLocalDate()
	    );
	    
	    Long totalExams = examRepository.countAllExams();

	    Long thisWeek = weeklyExamCount.createdThisMonth();
	    Long lastWeek = weeklyExamCount.createdLastMonth();
	    logger.info("This Week: {}, Last Week: {}", thisWeek, lastWeek);

	    double percentageChange;

	    if (lastWeek == 0) {
	        percentageChange = thisWeek > 0 ? 100.0 : 0.0;
	    } 
	    else {
	        percentageChange = ((thisWeek - lastWeek) / (double) lastWeek) * 100;
	    }

	    return new PresentAndPercentageIncreaseDTO(totalExams, percentageChange);
	}
	
	@Override
	public PresentAndPercentageIncreaseDTO fetchMonthlyCompletedExams() {
	    DateBoundaryDTO monthBoundary = miscellaneousService.getWeekBoundary();

	    DatePeriodWiseCountDTO weeklyExamCount = examRepository.findMonthlyExamCompleted(
	        monthBoundary.startOfThis().toLocalDate(),
	        monthBoundary.endOfThis().toLocalDate(),
	        monthBoundary.startOfLast().toLocalDate(),
	        monthBoundary.endOfLast().toLocalDate()
	    );

	    Long thisMonth = weeklyExamCount.createdThisMonth();
	    Long lastMonth = weeklyExamCount.createdLastMonth();
	    logger.info("This Month: {}, Last Month: {}", thisMonth, lastMonth);

	    double percentageChange;

	    if (lastMonth == 0) {
	        percentageChange = thisMonth > 0 ? 100.0 : 0.0;
	    } 
	    else {
	        percentageChange = ((thisMonth - lastMonth) / (double) lastMonth) * 100;
	    }

	    return new PresentAndPercentageIncreaseDTO(thisMonth, percentageChange);
	}
	
	public PresentAndPercentageIncreaseDTO calculateAveragePercentagePerMonth() {
	    LocalDate firstDayThisMonth = LocalDate.now().withDayOfMonth(1);
	    LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);

	    LocalDate start = firstDayLastMonth;
	    LocalDate end = firstDayThisMonth.plusMonths(1).minusDays(1);

	    List<StudentExamPercentageDTO> allResults =
	    		resultsRepository.findStudentExamPercentagesBetween(start, end);

	    
	    List<StudentExamPercentageDTO> thisMonthResults = new ArrayList<>();
	    List<StudentExamPercentageDTO> lastMonthResults = new ArrayList<>();

	    for (StudentExamPercentageDTO dto : allResults) {
	        if (dto.examDate().getMonth().equals(firstDayThisMonth.getMonth()) &&
	            dto.examDate().getYear() == firstDayThisMonth.getYear()) {
	            thisMonthResults.add(dto);
	        } else {
	            lastMonthResults.add(dto);
	        }
	    }

	    double thisMonthAverage = thisMonthResults.stream()
	        .mapToDouble(StudentExamPercentageDTO::percentage)
	        .average().orElse(0.0);

	    double lastMonthAverage = lastMonthResults.stream()
	        .mapToDouble(StudentExamPercentageDTO::percentage)
	        .average().orElse(0.0);

	    System.out.printf("This month avg: %.2f%%, Last month avg: %.2f%%%n",
	                      thisMonthAverage, lastMonthAverage);
	    
	    return new PresentAndPercentageIncreaseDTO(
	        (long) thisMonthAverage,
	        lastMonthAverage == 0 ? 100.0 : ((thisMonthAverage - lastMonthAverage) / lastMonthAverage) * 100
	    );
	}


	
	
//	public List<ExamScheduleDTO> fetchExamSchedule(LocalDate start, LocalDate end) {
//		if (start == null || end == null) {
//			throw new IllegalArgumentException("Start and end dates must not be null");
//		}
//		List<ExamScheduleDTO> examSchedules = examRepository.findExamSchedulesBetween(start, end);
//		if (examSchedules.isEmpty()) {
//			logger.info("No exam schedules found between {} and {}", start, end);
//		} else {
//			logger.info("Found {} exam schedules between {} and {}", examSchedules.size(), start, end);
//		}
//		
//		List<ExamScheduleDTO> updatedSchedules = examSchedules.stream()
//			    .map(e -> {
//			        LocalDateTime now = LocalDateTime.now();
//			        LocalDateTime start1 = LocalDateTime.of(e.getDate(), e.getTime());
//			        LocalDateTime end1 = start1.plusMinutes(e.getDuration());
//
//			        String status;
//			        if(e.getStatus().equals("cancelled")) {
//			            status = "cancelled";
//			        }
//			        else if (now.isBefore(start1)) {
//			            status = "upcoming";
//			        } else if (!now.isAfter(end1)) { // i.e., now is between start and end
//			            status = "ongoing";
//			        } else {
//			            status = "completed";
//			        }
//
//			        return new ExamScheduleDTO(
//			            e.getCode(),
//			            e.getTitle(),
//			            e.getDate(),
//			            e.getTime(),
//			            e.getDuration(),
//			            e.getStudents(),
//			            status
//			        );
//			    })
//			    .toList();
//		
//		return updatedSchedules;
//	}
	
	public List<ExamScheduleDTO> fetchExamSchedule(LocalDate start, LocalDate end) {
	    List<ExamScheduleDTO> exams = examRepository.findBasicExamSchedules(start, end);
	    List<Object[]> studentCountsRaw = examStudentRepository.findStudentCountsByExam(start, end);
	    List<Object[]> resultCountsRaw = resultsRepository.findResultCountsByExam(start, end);

	    Map<Integer, Long> studentCounts = studentCountsRaw.stream()
	        .collect(Collectors.toMap(
	            row -> (Integer) row[0],
	            row -> (Long) row[1]
	        ));

	    Map<Integer, Long> resultCounts = resultCountsRaw.stream()
	        .collect(Collectors.toMap(
	            row -> (Integer) row[0],
	            row -> (Long) row[1]
	        ));

	    LocalDateTime now = LocalDateTime.now();

	    return exams.stream().map(exam -> {
	        Long studentCount = studentCounts.getOrDefault(exam.getId(), 0L);
	        Long resultCount = resultCounts.getOrDefault(exam.getId(), 0L);

	        LocalDateTime examStart = LocalDateTime.of(exam.getDate(), exam.getTime());
	        LocalDateTime examEnd = examStart.plusMinutes(exam.getDuration());

	        String status;
	        if (resultCount == 0 && examStart.isBefore(now)) {
	            status = "cancelled";
	        } else if (now.isBefore(examStart)) {
	            status = "upcoming";
	        } else if (!now.isAfter(examEnd)) {
	            status = "ongoing";
	        } else {
	            status = "completed";
	        }

	        return new ExamScheduleDTO(
	            exam.getId(),
	            exam.getCode(),
	            exam.getTitle(),
	            exam.getDate(),
	            exam.getTime(),
	            exam.getDuration(),
	            studentCount,
	            resultCount,
	            status
	        );
	    }).toList();
	}
	
	
	@Override
	public List<LeaderBoardPayloadDTO> fetchLeaderBoard(String examCode) {
		List<LeaderboardDTO> leaderBoard = resultsRepository.findLeaderboardByExamCode(examCode, PageRequest.of(0, 5));
		return leaderBoard.stream()
				.map(LeaderBoardPayloadDTO::new)
				.toList();
	}
	
	public List<FieldWisePerformanceDTO> calculateExamWisePercentage(int pageNo, int pageSize) {
	    List<FieldWisePerformanceDTO> results = resultsRepository.calculateExamWisePerformance(Pageable.unpaged());

	    Map<String, List<FieldWisePerformanceDTO>> groupedByExam = results.stream()
	        .collect(Collectors.groupingBy(FieldWisePerformanceDTO::code));

	    List<FieldWisePerformanceDTO> averaged = groupedByExam.entrySet().stream()
	        .map(entry -> {
	            String examCode = entry.getKey();
	            List<FieldWisePerformanceDTO> performances = entry.getValue();
	            String examName = performances.get(0).name();

	            double avg = performances.stream()
	                .mapToDouble(FieldWisePerformanceDTO::averagePercentage)
	                .average()
	                .orElse(0.0);

	            return new FieldWisePerformanceDTO(
	                examCode,
	                examName,
	                Math.round(avg * 100.0) / 100.0
	            );
	        })
	        .sorted(Comparator.comparing(FieldWisePerformanceDTO::averagePercentage).reversed())
	        .toList();

	    int fromIndex = pageNo * pageSize;
	    int toIndex = Math.min(fromIndex + pageSize, averaged.size());

	    if (fromIndex >= averaged.size()) return List.of();

	    return averaged.subList(fromIndex, toIndex);
	}
	
	
	@Override
	public Map<String, Double> getPassFailPercentage() {
		LocalDate today = LocalDate.now();
		List<StudentExamPercentageDTO> percentages = resultsRepository.findStudentExamPercentagesOfPastExams(today);
		//Add 1 to pass variable where percentage is greater than 40.0 and 0 to fail variable where percentage is less than 40.0
		Map<Boolean, Long> resultCounts = percentages.stream()
			    .collect(Collectors.partitioningBy(
			        p -> p.percentage() >= 40.0,
			        Collectors.counting()
			    ));

			long passCount = resultCounts.getOrDefault(true, 0L);
			long failCount = resultCounts.getOrDefault(false, 0L);	
			
			double passpercentage = (double) passCount / (passCount + failCount) * 100;
			double failpercentage = (double) failCount / (passCount + failCount) * 100;
			Map<String, Double> passFailMap = Map.of(
			    "Pass", passpercentage,
			    "Fail", failpercentage
			);
			
			return passFailMap;
			

	}
	
	
	@Override
	public ExamCompletionStatusDTO getExamCompletionStatusDTO() {
		return examRepository.findExamCompletionStatusSummary();
	}






}
