package com.cozentus.oes.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.AnswerDTO;
import com.cozentus.oes.dto.LeaderboardEntryDTO;
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

import lombok.RequiredArgsConstructor;

@Service
public class ResultsServiceImpl implements ResultsService {
 
	 private final ExamRepository examRepo;
	    private final QuestionBankRepository questionRepo;
	    private final ExamStudentRepository examStudentRepo;
	    private final ExamSectionRepository examSectionRepo;
	    private final ResultsRepository resultsRepo;
	    private final AuthenticationService authenticationService;
	    private final UserInfoRepository userInfoRepository;
	    
	    public ResultsServiceImpl(
	            ExamRepository examRepo,
	            QuestionBankRepository questionRepo,
	            ExamStudentRepository examStudentRepo,
	            ExamSectionRepository examSectionRepo,
	            ResultsRepository resultsRepo,
	            AuthenticationService authenticationService,
	            UserInfoRepository userInfoRepository) {
	        this.examRepo = examRepo;
	        this.questionRepo = questionRepo;
	        this.examStudentRepo = examStudentRepo;
	        this.examSectionRepo = examSectionRepo;
	        this.resultsRepo = resultsRepo;
	        this.authenticationService = authenticationService;
	        this.userInfoRepository = userInfoRepository;
	    }
 

    @Override
    @Transactional
    public void submitExam(String examCode, List<AnswerDTO> answers) {
        Integer studentId = authenticationService.getCurrentUserDetails().getLeft();

        Exam exam = examRepo.findByCode(examCode)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + examCode));

        UserInfo student1 = userInfoRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        ExamStudent student = examStudentRepo
                .findByExamAndStudent(exam, student1)
                .orElseThrow(() -> new ResourceNotFoundException(examCode));

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
                        String correct = questionMap.get(a.questionCode()).getCorrectOption().toString();
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

    @Override
    public List<LeaderboardEntryDTO> getLeaderboardForExam(String examCode) {
        // Find exam by code
        Exam exam = examRepo.findByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Get all students assigned to this exam
        List<ExamStudent> students = examStudentRepo.findByExamId(exam.getId());

        // Prepare map studentId -> [score, time]
        Map<Integer, Integer> studentTotalScore = new HashMap<>();
        Map<Integer, Integer> studentTotalTime = new HashMap<>();

        for (ExamStudent es : students) {
            List<Results> results = resultsRepo.findByExamStudentId(es.getId());
            int score = results.stream().mapToInt(Results::getMarksScored).sum();
            int time = results.stream().mapToInt(r -> (int)r.getTimeTakenInSeconds()).sum();
            studentTotalScore.put(es.getStudent().getId(), score);
            studentTotalTime.put(es.getStudent().getId(), time);
        }

        // Build DTO list and sort by score desc, time asc
        List<LeaderboardEntryDTO> leaderboard = students.stream().map(es -> {
            UserInfo student = es.getStudent();
            int score = studentTotalScore.getOrDefault(student.getId(), 0);
            int time = studentTotalTime.getOrDefault(student.getId(), 0);
            return LeaderboardEntryDTO.builder()
                    .studentName(student.getName())
                    .score((long) score)
                    .timeTaken((long) time)
                    .build();
        }).sorted((a, b) -> {
            int cmp = b.getScore().compareTo(a.getScore());
            if (cmp != 0) return cmp;
            return a.getTimeTaken().compareTo(b.getTimeTaken());
        }).collect(Collectors.toList());


        // Assign ranks
        int rank = 1;
        for (LeaderboardEntryDTO entry : leaderboard) {
            entry.setRank(rank++);
        }

        return leaderboard;
    }
}
