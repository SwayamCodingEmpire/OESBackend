package com.cozentus.oes.serviceImpl;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.ExamQuestionRequestDTO;
import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamQuestion;
import com.cozentus.oes.entities.QuestionBank;
import com.cozentus.oes.repositories.ExamQuestionRepository;
import com.cozentus.oes.repositories.ExamRepository;
import com.cozentus.oes.repositories.QuestionBankRepository;
import com.cozentus.oes.services.ExamQuestionService;
import com.cozentus.oes.services.QuestionBankService;

@Service
public class ExamQuestionServiceImpl implements ExamQuestionService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;
    
    @Autowired
    private QuestionBankService questionBankService;

    @Transactional
    @Override
    public void addQuestionsToExam(String examCode, ExamQuestionRequestDTO requestDTO) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));

        for (String questionCode : requestDTO.getQuestionCodes()) {
            QuestionBank question = questionBankRepository.findByCode(questionCode)
                    .orElseThrow(() -> new RuntimeException("Question not found with code: " + questionCode));

            boolean exists = examQuestionRepository.findByExamAndQuestion(exam, question).isPresent();
            if (!exists) {
                ExamQuestion examQuestion = ExamQuestion.builder()
                        .exam(exam)
                        .question(question)
                        .enabled(true)
                        .build();
                examQuestionRepository.save(examQuestion);
            }
        }
    }

    @Transactional
    @Override
    public void removeQuestionFromExam(String examCode, String questionCode) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));
        QuestionBank question = questionBankRepository.findByCode(questionCode)
                .orElseThrow(() -> new RuntimeException("Question not found with code: " + questionCode));
        examQuestionRepository.deleteByExamAndQuestion(exam, question);
    }

    @Override
    public List<QuestionBankDTO> getAllQuestionsOfExam(String examCode) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));

        return examQuestionRepository.findByExam(exam).stream()
                .map(eq -> new QuestionBankDTO(eq.getQuestion()))
                .collect(Collectors.toList());
    }
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void addInstantExam(String examCode, List<QuestionBankDTO> questionBankDTOs) {
    	questionBankService.bulkInsertQuestions(questionBankDTOs, "INS1");
    	List<String> codes = questionBankDTOs.stream().map(QuestionBankDTO::code).collect(Collectors.toList());
    	addQuestionsToExam(examCode,new ExamQuestionRequestDTO(codes));
	
    }
    
    
}
