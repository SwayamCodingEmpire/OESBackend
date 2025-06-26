package com.cozentus.oes.serviceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.CodesDTO;
import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.dto.ExamQuestionkInsertionDTO;
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
import com.cozentus.oes.services.AuthenticationService;
import com.cozentus.oes.services.ExamDataService;
import com.cozentus.oes.services.ExamService;
import com.cozentus.oes.services.QuestionBankService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ExamDataServiceImpl implements ExamDataService {
private final Logger logger = LoggerFactory.getLogger(ExamDataServiceImpl.class);
    
    private final AuthenticationService authenticationService;
    private final ExamRepository examRepository;
    private final QuestionBankRepository questionBankRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionBankService questionBankService;
    private final ExamStudentRepository examStudentRepository;
    private final UserInfoRepository userInfoRepository;
    private final ExamService examService;

    @PersistenceContext
    private EntityManager entityManager;
    
    public ExamDataServiceImpl(
            AuthenticationService authenticationService,
            ExamRepository examRepository,
            QuestionBankRepository questionBankRepository,
            ExamQuestionRepository examQuestionRepository,
            QuestionBankService questionBankService,
            ExamStudentRepository examStudentRepository,
            UserInfoRepository userInfoRepository,
            ExamService examService) {
        this.authenticationService = authenticationService;
        this.examRepository = examRepository;
        this.questionBankRepository = questionBankRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionBankService = questionBankService;
        this.examStudentRepository = examStudentRepository;
        this.userInfoRepository = userInfoRepository;
        this.examService = examService;
    }
    


    @Transactional
    @Override
    public void addQuestionsToExam(String examCode, CodesDTO codesDTO) {
    	
    	String username = authenticationService.getCurrentUserDetails().getRight();
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));
        
        LocalDateTime examStartDateTime = LocalDateTime.of(exam.getExamDate(), exam.getExamTime());

        // Check if the exam has already started
        if (exam.getExamDate() != null && exam.getExamTime() != null &&
            examStartDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot add students to an exam that has already started.");
        }
        List<ExamQuestion> examQuestions = new ArrayList<>();
        logger.info("Adding questions to exam: {}", examCode);
        List<ExamQuestionkInsertionDTO> examQuestionkInsertionDTO = questionBankRepository.findAllByCodeInAndEnabledTrue(codesDTO.codes());
        int totalMarks = 0;
        int totalDuration = 0;

        for (var question : examQuestionkInsertionDTO) {
            QuestionBank questionBank = entityManager.getReference(QuestionBank.class, question.id());

            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExam(exam);
            examQuestion.setQuestion(questionBank);
            examQuestion.setCreatedBy(username);

            totalMarks += questionBank.getMarks() == null ? 0 : questionBank.getMarks();
            totalDuration += questionBank.getDuration()==null ? 0 : questionBank.getDuration();
            examQuestions.add(examQuestion);

            // exam.getExamQuestions().add(examQuestion); // if needed
        }

        exam.setTotalMarks(totalMarks);
        exam.setDuration(totalDuration);
        exam.setUpdatedBy(username);
        examQuestionRepository.saveAll(examQuestions);
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
    	String username = authenticationService.getCurrentUserDetails().getRight();
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));
        
        if (requestDTO.codes() == null || requestDTO.codes().isEmpty()) {
			throw new RuntimeException("No student codes provided.");
		}
        
     // Combine start date and time
        LocalDateTime examStartDateTime = LocalDateTime.of(exam.getExamDate(), exam.getExamTime());

        // Check if the exam has already started
        if (exam.getExamDate() != null && exam.getExamTime() != null &&
            examStartDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot add students to an exam that has already started.");
        }
        
        List<UserInfo> students = userInfoRepository.findAllByCodeInAndEnabledTrue(requestDTO.codes());
        List<ExamStudent> saveExamStudents = new ArrayList<>();
        
        for (UserInfo student : students) {
        	ExamStudent examStudent = new ExamStudent();
			examStudent.setExam(exam);
			examStudent.setStudent(student);
			examStudent.setCreatedBy(username);
			saveExamStudents.add(examStudent);
			
		}
        if (saveExamStudents.isEmpty()) {
			throw new RuntimeException("No valid students found to add to the exam.");
		}
		
		examStudentRepository.saveAll(saveExamStudents);
		logger.info("Added {} students to exam: {}", saveExamStudents.size(), examCode);
    }
    
    @Override
    @Transactional
    public List<UserInfoDTO> getAllStudentsOfExam(String examCode) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found with code: " + examCode));

        System.out.println("Exam found: " + exam.getCode());
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

	@Override
	public List<ExamDTO> getAllExamsByStudent(Integer id) {
		// TODO Auto-generated method stub
		return examStudentRepository.findAllByStudentId(id);
			
	}
    
    
}
