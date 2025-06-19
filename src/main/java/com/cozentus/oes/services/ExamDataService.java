package com.cozentus.oes.services;

import com.cozentus.oes.dto.CodesDTO;
import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.dto.UserInfoDTO;

import java.util.List;

public interface ExamDataService {

    void addQuestionsToExam(String examCode, CodesDTO requestDTO);

    void removeQuestionFromExam(String examCode, String questionCode);

    List<QuestionBankDTO> getAllQuestionsOfExam(String examCode);
    
    void addStudentsToExam(String examCode, CodesDTO requestDTO);
    
    List<UserInfoDTO> getAllStudentsOfExam(String examCode);
    
    void addInstantExam(String examCode, List<QuestionBankDTO> questionBankDTOs);
}
