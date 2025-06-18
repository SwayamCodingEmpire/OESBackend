package com.cozentus.oes.services;

import com.cozentus.oes.dto.ExamQuestionRequestDTO;
import com.cozentus.oes.dto.QuestionBankDTO;

import java.util.List;

public interface ExamQuestionService {

    void addQuestionsToExam(String examCode, ExamQuestionRequestDTO requestDTO);

    void removeQuestionFromExam(String examCode, String questionCode);

    List<QuestionBankDTO> getAllQuestionsOfExam(String examCode);
}
