package com.cozentus.oes.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cozentus.oes.dto.LeaderboardEntryDTO;
import com.cozentus.oes.entities.Results;

public interface ResultsRepository extends JpaRepository<Results, Integer> {
    List<Results> findByExamStudent_Exam_CodeAndExamStudent_Student_Id(String examCode, Integer studentId);
    
 // To get all results by ExamStudent or Exam
    List<Results> findByExamStudent_Exam_Id(Integer examId);

    // Or to get leaderboard:
    @Query("SELECT new com.cozentus.oes.dto.LeaderboardEntryDTO(" +
           "es.student.name, SUM(r.marksScored), SUM(r.timeTakenInSeconds), 0) " +
           "FROM Results r " +
           "JOIN r.examStudent es " +
           "JOIN r.examSection sec " +
           "WHERE sec.exam.code = :examCode " +
           "GROUP BY es.student.id, es.student.name " +
           "ORDER BY SUM(r.marksScored) DESC, SUM(r.timeTakenInSeconds) ASC")
    List<LeaderboardEntryDTO> getLeaderboardForExam(@Param("examCode") String examCode);
    
    List<Results> findByExamStudentId(Integer examStudentId);
    
    

}
