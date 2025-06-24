package com.cozentus.oes.serviceImpl;
 
import java.time.LocalDate;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.Optional;

import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
 
import com.cozentus.oes.dto.AnswerDTO;

import com.cozentus.oes.dto.SectionSummaryDTO;

import com.cozentus.oes.entities.Exam;

import com.cozentus.oes.entities.ExamSection;

import com.cozentus.oes.entities.ExamStudent;

import com.cozentus.oes.entities.QuestionBank;

import com.cozentus.oes.entities.Results;

import com.cozentus.oes.entities.UserInfo;

import com.cozentus.oes.exceptions.ResourceNotFoundException;

import com.cozentus.oes.repositories.ExamRepository;

import com.cozentus.oes.repositories.ExamSectionRepository;

import com.cozentus.oes.repositories.ExamStudentRepository;

import com.cozentus.oes.repositories.QuestionBankRepository;

import com.cozentus.oes.repositories.ResultsRepository;

import com.cozentus.oes.repositories.UserInfoRepository;

import com.cozentus.oes.services.AuthenticationService;

import com.cozentus.oes.services.ResultsService;
 
@Service

public class ResultsServiceImpl implements ResultsService {
 
    @Autowired 

    private ExamRepository examRepo;

    @Autowired 

    private QuestionBankRepository questionRepo;

    @Autowired 

    private ExamStudentRepository examStudentRepo;

    @Autowired 

    private ExamSectionRepository examSectionRepo;

    @Autowired 

    private ResultsRepository resultsRepo;

    @Autowired

    private AuthenticationService authenticationService;

    @Autowired

    private UserInfoRepository userInfoRepository;
 
    @Override

    @Transactional

    public void submitExam(String examCode, List<AnswerDTO> answers) {

    	Integer studentId = authenticationService.getCurrentUserDetails().getLeft();

        Exam exam = examRepo.findByCode(examCode)

            .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + examCode));

        UserInfo student1 = userInfoRepository.findById(studentId).orElseThrow(()-> new ResourceNotFoundException("Student not found with ID: " + studentId));

        ExamStudent student = examStudentRepo

            .findByExamAndStudent(exam,student1)

            .orElseThrow(()->new ResourceNotFoundException(examCode));
 
        Map<String, QuestionBank> questionMap = questionRepo

            .findAllByCodeIn(answers.stream()

                .map(AnswerDTO::questionCode)

                .collect(Collectors.toList()))

            .stream()

            .collect(Collectors.toMap(QuestionBank::getCode, q -> q));
 
        Map<ExamSection, List<AnswerDTO>> answersBySection = new HashMap<>();

        for (AnswerDTO a : answers) {

            QuestionBank qb = Optional.ofNullable(questionMap.get(a.questionCode()))

                .orElseThrow(() -> new ResourceNotFoundException(

                    "Question not found: " + a.questionCode()));
 
            ExamSection sec = examSectionRepo

                .findByExamAndSectionCode(exam, qb.getTopic().getCode())

                .orElseThrow(() -> new ResourceNotFoundException(

                    "Section not found for topic: " + qb.getTopic().getCode()));
 
            answersBySection

                .computeIfAbsent(sec, s -> new java.util.ArrayList<>())

                .add(a);

        }
 
        for (Map.Entry<ExamSection, List<AnswerDTO>> entry : answersBySection.entrySet()) {

            ExamSection section = entry.getKey();

            List<AnswerDTO> list = entry.getValue();
 
            int sectionMarks = list.stream()

                .mapToInt(a -> {

                    String submitted = a.answer();

                    String correct   = questionMap.get(a.questionCode()).getCorrectOption().toString();

                    return correct.equalsIgnoreCase(submitted)

                        ? questionMap.get(a.questionCode()).getMarks()

                        : 0;

                })

                .sum();
 
            resultsRepo.save(Results.builder()

                .examStudent(student)

                .examSection(section)

                .marksScored(sectionMarks)

                .publishDate(LocalDate.now())

                .enabled(true)

                .build()

            );

        }

    }

    @Override

    @Transactional(readOnly = true)

    public List<SectionSummaryDTO> getSummaryByExamCode(String examCode) {

        Integer studentId = authenticationService.getCurrentUserDetails().getLeft();

        Exam exam = examRepo.findByCode(examCode)

            .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + examCode));
 
        return resultsRepo.findByExamStudent_Exam_CodeAndExamStudent_Student_Id(examCode, studentId).stream()

            .map(r -> {

                var sec = r.getExamSection();

                return new SectionSummaryDTO(

                    sec.getSection().getName(),

                    sec.getTotalMarks(),

                    r.getMarksScored()

                );

            })

            .collect(Collectors.toList());

    }
 
 
}

 