// src/main/java/com/cozentus/oes/services/ResultsService.java
package com.cozentus.oes.services;
 
import java.util.List;
import com.cozentus.oes.dto.AnswerDTO;
import com.cozentus.oes.dto.LeaderboardEntryDTO;
import com.cozentus.oes.dto.SectionSummaryDTO;
 
public interface ResultsService {
    /**
     * Calculate marks, persist ExamStudent & Results rows.
     *
     * @param examCode the code of the exam being submitted
     * @param answers  the list of AnswerDTO objects (questionCode + answer)
     */
    void submitExam(String examCode, List<AnswerDTO> answers);
 
 
	List<SectionSummaryDTO> getSummaryByExamCode(String examCode);
    List<LeaderboardEntryDTO> getLeaderboardForExam(String examCode);

}