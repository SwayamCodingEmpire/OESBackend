package com.cozentus.oes.serviceImpl;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.CodesDTO;
import com.cozentus.oes.dto.ExamSectionDTO;
import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.ExamQuestion;
import com.cozentus.oes.entities.ExamStudent;
import com.cozentus.oes.entities.QuestionBank;
import com.cozentus.oes.entities.UserInfo;
import com.cozentus.oes.repositories.ExamQuestionRepository;
import com.cozentus.oes.repositories.ExamRepository;
import com.cozentus.oes.repositories.ExamStudentRepository;
import com.cozentus.oes.repositories.QuestionBankRepository;
import com.cozentus.oes.repositories.UserInfoRepository;
import com.cozentus.oes.services.ExamDataService;
import com.cozentus.oes.services.ExamService;
import com.cozentus.oes.services.QuestionBankService;

@Service
public class ExamDataServiceImpl implements ExamDataService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;
    
    @Autowired
    private QuestionBankService questionBankService;
    
    @Autowired
    private ExamStudentRepository examStudentRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private ExamService examService;

    @Transactional
    @Override
    public void addQuestionsToExam(String examCode, CodesDTO requestDTO) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));

        for (String questionCode : requestDTO.codes()) {
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
    @Override
    public void addInstantExam(String examCode, List<QuestionBankDTO> questionBankDTOs) {
        // Insert questions under topic "INS1"
        questionBankService.bulkInsertQuestions(questionBankDTOs, "INS1");

        // Extract question codes
        List<String> codes = questionBankDTOs.stream()
            .map(QuestionBankDTO::code)
            .collect(Collectors.toList());

        // Assign questions to exam
        addQuestionsToExam(examCode, new CodesDTO(codes));

        // Sum marks and duration
        int totalMarks = questionBankDTOs.stream()
            .mapToInt(QuestionBankDTO::marks)
            .sum();

        int totalDuration = questionBankDTOs.stream()
            .mapToInt(QuestionBankDTO::duration)
            .sum();

        // Create ExamSectionDTO with topicCode "INS1"
        ExamSectionDTO sectionDTO = new ExamSectionDTO("INS1", totalDuration, totalMarks);

        // Add section to the exam
        examService.addExamSection(List.of(sectionDTO), examCode);
    }

    
    @Transactional
    @Override
    public void addStudentsToExam(String examCode, CodesDTO requestDTO) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));

        for (String studentCode : requestDTO.codes()) {
            UserInfo student = userInfoRepository.findByCode(studentCode)
                    .orElseThrow(() -> new RuntimeException("Question not found with code: " + studentCode));

            boolean exists = examStudentRepository.findByExamAndStudent(exam, student).isPresent();
            if (!exists) {
                ExamStudent examQuestion = ExamStudent.builder()
                        .exam(exam)
                        .student(student)
                        .enabled(true)
                        .build();
                examStudentRepository.save(examQuestion);
            }
        }
    }
    
    @Override
    @Transactional
    public List<UserInfoDTO> getAllStudentsOfExam(String examCode) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));

        return examStudentRepository.findByExam(exam).stream()
        	    .map(examStudent -> new UserInfoDTO(examStudent.getStudent()))
        	    .collect(Collectors.toList());

    }

	@Override
	public void deleteStudentFromExam(String examCode, String studentCode) {
		examStudentRepository.deleteByExamIdAndStudentId(
				examRepository.findByCode(examCode)
				.orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode))
				.getId(), 
				userInfoRepository.findByCode(studentCode)
				.orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode))
				.getId());
		
	}
    
    
}
