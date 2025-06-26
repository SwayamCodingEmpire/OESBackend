package com.cozentus.oes.repositories;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamStudent;
import com.cozentus.oes.entities.UserInfo;

public interface ExamStudentRepository extends JpaRepository<ExamStudent, Integer>  {
	Optional<ExamStudent> findByExamAndStudent(Exam exam, UserInfo student);
	
	List<ExamStudent> findByExam(Exam exam);
	
	void deleteByExamIdAndStudentId(Integer examId, Integer studentId);
	


	
	   @Query("SELECT new com.cozentus.oes.dto.ExamDTO(e.code, e.name, e.examDate, e.examTime) " +
		       "FROM ExamStudent es " +
		       "LEFT JOIN es.exam e " +
		       "WHERE es.student.id = :studentId")
		List<ExamDTO> findAllByStudentId(@Param("studentId") Integer studentId);
	   

   	@Query("""
    	    SELECT es.exam.id, COUNT(DISTINCT es.student.id)
    	    FROM ExamStudent es
    	    WHERE es.exam.examDate BETWEEN :start AND :end
    	    GROUP BY es.exam.id
    	""")
    	List<Object[]> findStudentCountsByExam(@Param("start") LocalDate start, @Param("end") LocalDate end);

	// To get all exams for a student:
	   List<ExamStudent> findByStudentId(Integer studentId);
	   
	   List<ExamStudent> findByExamId(Integer examId);




}

