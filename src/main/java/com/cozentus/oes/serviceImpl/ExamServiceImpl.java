package com.cozentus.oes.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.dto.ExamSectionDTO;
import com.cozentus.oes.dto.IdAndCodeDTO;
import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamSection;
import com.cozentus.oes.entities.Topic;
import com.cozentus.oes.exceptions.ResourceNotFoundException;
import com.cozentus.oes.repositories.ExamRepository;
import com.cozentus.oes.repositories.ExamSectionRepository;
import com.cozentus.oes.repositories.TopicRepository;
import com.cozentus.oes.services.AuthenticationService;
import com.cozentus.oes.services.ExamService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ExamServiceImpl implements ExamService {
	@Autowired
	private AuthenticationService authenticationService;

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
    @CacheEvict(value = "exams", key = "'allexams'")
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
    @Cacheable(value = "exams", key = "'allexams'")
    public List<ExamDTO> getAllExams() {
        return examRepository.findAllByEnabledTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "exams", key = "'allexams'")
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
    	String username = authenticationService.getCurrentUserDetails().getRight();
        // Set the Exam reference using EntityManager
        Exam exam = examRepository.findByCode(examCode).orElseThrow(() -> new ResourceNotFoundException("Exam not found with code: " + examCode));
        
        List<IdAndCodeDTO> topicIdAndCodeDTOList  = topicRepository.findAllByCodeInAndEnabledTrue(
        		examSectionDTOs.stream()
				.map(ExamSectionDTO::topicCode)
				.toList()
				);
        
        if(topicIdAndCodeDTOList.size() != examSectionDTOs.size()) {
			throw new ResourceNotFoundException("One or more topics not found for the provided codes.");
		}
        
        Map<String, IdAndCodeDTO> topicMap = topicIdAndCodeDTOList.stream()
				.collect(Collectors.toMap(IdAndCodeDTO::code, dto -> dto));
        
        List<ExamSection> examSectionWithValidTopics = new ArrayList<>();
        for (ExamSectionDTO dto : examSectionDTOs) {
            IdAndCodeDTO topicDTO = topicMap.get(dto.topicCode());
            Topic topic = entityManager.getReference(Topic.class, topicDTO.id());
            
            ExamSection examSection = new ExamSection();
            examSection.setExam(exam);
            examSection.setSection(topic);
            examSection.setDuration(dto.duration());
            examSection.setTotalMarks(dto.totalMarks());
            examSection.setCreatedBy(username); // Set createdBy as needed
            // Create a new ExamSection

            // Save the ExamSection
            examSectionWithValidTopics.add(examSection);
        }
        
        examSectionRepository.saveAll(examSectionWithValidTopics);
    }


}
