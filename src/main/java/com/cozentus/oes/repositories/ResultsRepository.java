package com.cozentus.oes.repositories;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cozentus.oes.dto.LeaderboardDTO;
import com.cozentus.oes.dto.StudentExamPercentageDTO;
import com.cozentus.oes.dto.FieldWisePerformanceDTO;
import com.cozentus.oes.entities.Results;

public interface ResultsRepository extends JpaRepository<Results, Integer> {
    List<Results> findByExamStudent_Exam_CodeAndExamStudent_Student_Id(String examCode, Integer studentId);
    
    
    @Query(""
    		+ "SELECT new com.cozentus.oes.dto.FieldWisePerformanceDTO("
    		+ "t.code, "
    		+ "t.name, "
    		+ "ROUND(AVG((r.marksScored/es.totalMarks)*100),2) as averagePercentage"
    		+ ") "
    		+ "FROM Results r "
    		+ "LEFT JOIN r.examSection es "
    		+ "LEFT JOIN es.section t "
    		+ "GROUP BY t.code, t.name "
    		+ "ORDER BY averagePercentage DESC")
    List<FieldWisePerformanceDTO> calculateTopicWisePerformance(Pageable pageable);
    
    
    
    @Query("""
    		  SELECT new com.cozentus.oes.dto.StudentExamPercentageDTO(
    		    exs.student.id,
    		    e.id,
    		    e.examDate,
    		    SUM(r.marksScored),
    		    SUM(es.totalMarks),
    		    (SUM(r.marksScored) / SUM(es.totalMarks)) * 100.0
    		  )
    		  FROM Results r
    		  JOIN r.examSection es
    		  JOIN r.examStudent exs
    		  JOIN exs.exam e
    		  WHERE e.examDate BETWEEN :start AND :end
    		  GROUP BY exs.student.id, e.id, e.examDate
    		""")
    		List<StudentExamPercentageDTO> findStudentExamPercentagesBetween(
    		    @Param("start") LocalDate start,
    		    @Param("end") LocalDate end
    		);
    
	@Query("""
    	    SELECT es.exam.id, COUNT(DISTINCT r.id)
    	    FROM Results r
    	    JOIN r.examStudent es
    	    WHERE es.exam.examDate BETWEEN :start AND :end
    	    GROUP BY es.exam.id
    	""")
    	List<Object[]> findResultCountsByExam(@Param("start") LocalDate start, @Param("end") LocalDate end);
    	
    	@Query("""
    		    SELECT new com.cozentus.oes.dto.LeaderboardDTO(
    		        es.student.name,
    		        ROUND(CAST(SUM(r.marksScored) AS double) * 100.0 / CAST(SUM(esec.totalMarks) AS double), 2),
    		        SUM(r.timeTakenInSeconds)
    		    )
    		    FROM Results r
    		    JOIN r.examStudent es
    		    JOIN r.examSection esec
    		    WHERE es.exam.code = :examCode
    		    GROUP BY es.student.name
    		    ORDER BY ROUND(CAST(SUM(r.marksScored) AS double) * 100.0 / CAST(SUM(esec.totalMarks) AS double), 2) DESC
    		""")
    		List<LeaderboardDTO> findLeaderboardByExamCode(@Param("examCode") String examCode, Pageable pageable);
    	
//    	
//    	@Query("""
//			SELECT new com.cozentus.oes.dto.LeaderboardDTO(
//				es.student.name,
//				ROUND(CAST(SUM(r.marksScored) AS double) * 100.0 / CAST(SUM(esec.totalMarks) AS double), 2),
//				SUM(r.timeTakenInSeconds)
//			)
//			FROM Results r
//			JOIN r.examStudent es
//			JOIN r.examSection esec
//			WHERE es.exam.code = :examCode
//			GROUP BY es.student.name
//			ORDER BY ROUND(CAST(SUM(r.marksScored) AS double) * 100.0 / CAST(SUM(esec.totalMarks) AS double), 2) DESC
//		""")
    	
    	@Query("SELECT count(DISTINCT r.examSection.exam.code) FROM Results r")
    	Long findExamCodesWithResults();
    	
    	
        @Query("""
      		  SELECT new com.cozentus.oes.dto.StudentExamPercentageDTO(
      		    exs.student.id,
      		    e.id,
      		    e.examDate,
      		    SUM(r.marksScored),
      		    SUM(es.totalMarks),
      		    (SUM(r.marksScored) / SUM(es.totalMarks)) * 100.0
      		  )
      		  FROM Results r
      		  JOIN r.examSection es
      		  JOIN r.examStudent exs
      		  JOIN exs.exam e
      		  WHERE e.examDate >= :start
      		  GROUP BY exs.student.id, e.id, e.examDate
      		""")
      		List<StudentExamPercentageDTO> findStudentExamPercentagesSince(
      		    @Param("start") LocalDate start);
    	
        @Query("""
        	    SELECT new com.cozentus.oes.dto.FieldWisePerformanceDTO(
        	        e.code,
        	        e.name,
        	        SUM(r.marksScored) * 100.0 / SUM(es.totalMarks)
        	    )
        	    FROM Results r
        	    JOIN r.examStudent exs
        	    JOIN exs.exam e
        	    JOIN r.examSection es
        	    GROUP BY e.id, exs.student.id, e.code, e.name
        	""")
        List<FieldWisePerformanceDTO> calculateExamWisePerformance(Pageable pageable);
        
        
        
        @Query("""
        		  SELECT new com.cozentus.oes.dto.StudentExamPercentageDTO(
        		    exs.student.id,
        		    e.id,
        		    e.examDate,
        		    SUM(r.marksScored),
        		    SUM(es.totalMarks),
        		    (SUM(r.marksScored) / SUM(es.totalMarks)) * 100.0
        		  )
        		  FROM Results r
        		  JOIN r.examSection es
        		  JOIN r.examStudent exs
        		  JOIN exs.exam e
        		  WHERE e.examDate <= :end
        		  GROUP BY exs.student.id, e.id, e.examDate
        		""")
        		List<StudentExamPercentageDTO> findStudentExamPercentagesOfPastExams(
        		    @Param("end") LocalDate end);









    
    
    
}
