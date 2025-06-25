package com.cozentus.oes.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.DatePeriodWiseCountDTO;
import com.cozentus.oes.dto.ExamCompletionStatusDTO;
import com.cozentus.oes.dto.ExamScheduleDTO;
import com.cozentus.oes.entities.Exam;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Optional<Exam> findByCode(String code);
    void deleteByCode(String code);
    
    @Modifying
    @Transactional
    @Query("UPDATE EXAM e SET e.enabled = false WHERE e.code = :code")
    int softDeleteByCode(@Param("code") String code);
    
    List<Exam> findAllByEnabledTrue();
    
    
	
    @Query("""
    		  SELECT new com.cozentus.oes.dto.DatePeriodWiseCountDTO(
    		    SUM(CASE WHEN e.examDate BETWEEN :startOfThisWeek AND :endOfThisWeek THEN 1 ELSE 0 END),
    		    SUM(CASE WHEN e.examDate BETWEEN :startOfLastWeek AND :endOfLastWeek THEN 1 ELSE 0 END)
    		  )
    		  FROM EXAM e
    		  WHERE e.enabled = true
    		""")
    		DatePeriodWiseCountDTO findThisWeeklyExamCount(
    		    @Param("startOfThisWeek") LocalDate startOfThisWeek,
    		    @Param("endOfThisWeek") LocalDate endOfThisWeek,
    		    @Param("startOfLastWeek") LocalDate startOfLastWeek,
    		    @Param("endOfLastWeek") LocalDate endOfLastWeek
    		);
    
    
    
    @Query("""
    		  SELECT new com.cozentus.oes.dto.DatePeriodWiseCountDTO(
    		    SUM(CASE WHEN e.examDate BETWEEN :startOfThisMonth AND :endOfThisMonth THEN 1 ELSE 0 END),
    		    SUM(CASE WHEN e.examDate BETWEEN :startOfLastMonth AND :endOfLastMonth THEN 1 ELSE 0 END)
    		  )
    		  FROM EXAM e
    		  WHERE e.enabled = true
    		    AND EXISTS (
    		      SELECT 1
    		      FROM ExamSection es
    		      JOIN Results r ON r.examSection = es
    		      WHERE es.exam = e
    		    )
    		""")
    		DatePeriodWiseCountDTO findMonthlyExamCompleted(
    		    @Param("startOfThisMonth") LocalDate startOfThisMonth,
    		    @Param("endOfThisMonth") LocalDate endOfThisMonth,
    		    @Param("startOfLastMonth") LocalDate startOfLastMonth,
    		    @Param("endOfLastMonth") LocalDate endOfLastMonth
    		);
    
    
//    @Query("""
//    	    SELECT new com.cozentus.oes.dto.ExamScheduleDTO(
//    	        e.code,
//    	        e.name,
//    	        e.examDate,
//    	        e.examTime,
//    	        e.duration,
//    	        COUNT(DISTINCT es.id),
//    	        COUNT(DISTINCT r.id),
//    	        'N/A' as status
//    	    )
//    	    FROM EXAM e
//    	    LEFT JOIN e.examStudents es
//    	    LEFT JOIN Results r ON r.examStudent.exam = e
//    	    WHERE e.enabled = true AND e.examDate BETWEEN :start AND :end
//    	    GROUP BY e.code, e.name, e.examDate, e.examTime, e.duration, status
//    	""")
//    	List<ExamScheduleDTO> findExamSchedulesWithResultCountBetween(
//    	    @Param("start") LocalDate start,
//    	    @Param("end") LocalDate end
//    	);
    
    @Query("""
    	    SELECT new com.cozentus.oes.dto.ExamScheduleDTO(
    	        e.id, e.code, e.name, e.examDate, e.examTime, e.duration,
    	        0L, 0L, 'N/A'
    	    )
    	    FROM EXAM e
    	    WHERE e.enabled = true AND e.examDate BETWEEN :start AND :end
    	""")
    	List<ExamScheduleDTO> findBasicExamSchedules(@Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT COUNT(e) FROM EXAM e")
    Long countAllExams();
    
    
    
    @Query("""
    	    SELECT new com.cozentus.oes.dto.ExamCompletionStatusDTO(
    	        SUM(CASE WHEN e.examDate < CURRENT_DATE THEN 1 ELSE 0 END), 
    	        SUM(CASE WHEN e.examDate = CURRENT_DATE THEN 1 ELSE 0 END), 
    	        SUM(CASE WHEN e.examDate > CURRENT_DATE THEN 1 ELSE 0 END)     
    	    )
    	    FROM EXAM e
    	""")
    	ExamCompletionStatusDTO findExamCompletionStatusSummary();

    
    








    
}

