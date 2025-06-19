package com.cozentus.oes.services;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.dto.ExamSectionDTO;

import java.util.List;

public interface ExamService {
    ExamDTO createExam(ExamDTO examDTO);
    ExamDTO updateExam(String code, ExamDTO examDTO);
    ExamDTO getExamByID(String code);
    List<ExamDTO> getAllExams();
    void deleteExam(String code);
    void addExamSection(List<ExamSectionDTO> examSectionDTOs, String examCode);
}
