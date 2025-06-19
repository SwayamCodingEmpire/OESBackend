package com.cozentus.oes.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.dto.ExamSectionDTO;
import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamSection;
import com.cozentus.oes.entities.Topic;
import com.cozentus.oes.exceptions.ResourceNotFoundException;
import com.cozentus.oes.repositories.ExamRepository;
import com.cozentus.oes.repositories.ExamSectionRepository;
import com.cozentus.oes.repositories.TopicRepository;
import com.cozentus.oes.services.ExamService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ExamSectionRepository examSectionRepository;
    @PersistenceContext
    private EntityManager entityManager;

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
        exam.setCode(dto.code());
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
    
    @Transactional
    public void addExamSection(List<ExamSectionDTO> examSectionDTOs, String examCode) {
        // Set the Exam reference using EntityManager
        Exam exam = examRepository.findByCode(examCode).orElseThrow(() -> new ResourceNotFoundException("Exam not found with code: " + examCode));

        // Iterate through the list of ExamSectionDTOs
        for (ExamSectionDTO dto : examSectionDTOs) {
            // Set the Topic reference using EntityManager
            Topic topic = topicRepository.findByCode(dto.topicCode())
				.orElseThrow(() -> new ResourceNotFoundException("Topic not found with ID: " + dto.topicCode()));

            // Create a new ExamSection
            ExamSection examSection = ExamSection.builder()
                .exam(exam)
                .section(topic)
                .duration(dto.duration())
                .totalMarks(dto.totalMarks())
                .enabled(true)
                .build();

            // Save the ExamSection
            examSectionRepository.save(examSection);
        }
    }


}
