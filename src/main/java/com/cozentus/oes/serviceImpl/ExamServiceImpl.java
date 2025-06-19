package com.cozentus.oes.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.exceptions.ResourceNotFoundException;
import com.cozentus.oes.repositories.ExamRepository;
import com.cozentus.oes.services.ExamService;

import jakarta.transaction.Transactional;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Override
    public ExamDTO createExam(ExamDTO dto) {
        Exam exam = new Exam();
        exam.setCode(dto.code());
        exam.setName(dto.name());
        exam.setExamDate(dto.examDate());
        exam.setExamTime(dto.examTime());
        return toDTO(examRepository.save(exam));
    }

    @Override
    public ExamDTO updateExam(String code, ExamDTO dto) {
        Exam exam = examRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + code));

        exam.setName(dto.name());
        exam.setExamDate(dto.examDate());
        exam.setExamTime(dto.examTime());
        return toDTO(examRepository.save(exam));
    }

    @Override
    public ExamDTO getExamByID(String code) {
        Exam exam = examRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + code));
        return toDTO(exam);
    }

    @Override
    public List<ExamDTO> getAllExams() {
        return examRepository.findAllByEnabledTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExam(String code) {
        if (1!=examRepository.softDeleteByCode(code)) {
			throw new ResourceNotFoundException("Exam not found with code: " + code);
		}
    }

    private ExamDTO toDTO(Exam exam) {
        return new ExamDTO(
				exam.getCode(),
				exam.getName(),
				exam.getExamDate(),
				exam.getExamTime()
		);
    }
}
