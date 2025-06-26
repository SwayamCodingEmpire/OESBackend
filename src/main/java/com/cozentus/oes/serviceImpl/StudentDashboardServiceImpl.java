package com.cozentus.oes.serviceImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.*;
import com.cozentus.oes.entities.*;
import com.cozentus.oes.repositories.*;
import com.cozentus.oes.services.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final UserInfoRepository userInfoRepository;
    private final ExamStudentRepository examStudentRepository;
    private final ResultsRepository resultsRepository;
    private final ExamSectionRepository examSectionRepository;
    private final ExamRepository examRepository;
    private final TopicRepository topicRepository;

    @Override
    public StudentDashboardDTO getDashboardForStudent(Integer studentId) {
        UserInfo student = getUser(studentId);

        UserInfoDTO studentInfo = UserInfoDTO.builder()
            .code(student.getCode())
            .name(student.getName())
            .email(student.getEmail())
            .phoneNo(student.getPhoneNo())
            .build();

        List<ExamStatsDTO> attemptedExams = getAttemptedExams(studentId);
        List<TopicStatsDTO> topicStats = getTopicStats(studentId);
        List<DifficultyStatsDTO> difficultyStats = getDifficultyStats(attemptedExams);
        List<UpcomingExamDTO> upcomingExams = getUpcomingExams(studentId);

        Integer totalStudents = (int) userInfoRepository.count();
        Integer classRank = calculateClassRank(studentId);

        return StudentDashboardDTO.builder()
            .studentInfo(studentInfo)
            .attemptedExams(attemptedExams)
            .topicStats(topicStats)
            .difficultyStats(difficultyStats)
            .upcomingExams(upcomingExams)
            .classRank(classRank)
            .totalStudents(totalStudents)
            .build();
    }

    // --- Helper Methods ---

    private UserInfo getUser(Integer studentId) {
        return userInfoRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    private List<ExamStatsDTO> getAttemptedExams(Integer studentId) {
        List<ExamStudent> examStudents = examStudentRepository.findByStudentId(studentId);

        return examStudents.stream().map(es -> {
            Exam exam = es.getExam();
            List<Results> results = resultsRepository.findByExamStudentId(es.getId());

            int totalScore = results.stream().mapToInt(Results::getMarksScored).sum();
            int maxScore = examSectionRepository.findByExamId(exam.getId())
                .stream().mapToInt(ExamSection::getTotalMarks).sum();
            int totalTime = results.stream()
                .mapToInt(r -> r.getTimeTakenInSeconds() != null ? r.getTimeTakenInSeconds().intValue() : 0)
                .sum();

            // Difficulty breakdown placeholder (implement real logic if needed)
            DifficultyBreakdownDTO diffBreak = DifficultyBreakdownDTO.builder()
                .easy(0).medium(0).hard(0).build();

            int rank = getExamRank(exam.getId(), es.getId());

            return ExamStatsDTO.builder()
                .examCode(exam.getCode())
                .examName(exam.getName())
                .date(exam.getExamDate().toString())
                .topic(exam.getName()) // Replace with actual topic mapping if needed
                .score(totalScore)
                .maxScore(maxScore)
                .timeTaken(totalTime)
                .rank(rank)
                .difficultyBreakdown(diffBreak)
                .build();
        }).collect(Collectors.toList());
    }

    private int getExamRank(Integer examId, Integer studentExamId) {
        List<ExamStudent> students = examStudentRepository.findByExamId(examId);
        Map<Integer, Integer> scores = new HashMap<>();
        Map<Integer, Integer> times = new HashMap<>();
        for (ExamStudent es : students) {
            List<Results> results = resultsRepository.findByExamStudentId(es.getId());
            int totalScore = results.stream().mapToInt(Results::getMarksScored).sum();
            int totalTime = results.stream()
                .mapToInt(r -> r.getTimeTakenInSeconds() != null ? r.getTimeTakenInSeconds().intValue() : 0)
                .sum();
            scores.put(es.getId(), totalScore);
            times.put(es.getId(), totalTime);
        }
        List<Integer> rankedStudentIds = scores.entrySet().stream()
            .sorted((a, b) -> {
                int cmp = b.getValue().compareTo(a.getValue());
                if (cmp != 0) return cmp;
                return times.get(a.getKey()).compareTo(times.get(b.getKey()));
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        return rankedStudentIds.indexOf(studentExamId) + 1;
    }

    private List<TopicStatsDTO> getTopicStats(Integer studentId) {
        List<ExamStudent> examStudents = examStudentRepository.findByStudentId(studentId);

        return topicRepository.findAll().stream().map(topic -> {
            List<ExamSection> sections = examSectionRepository.findBySectionId(topic.getId());
            Set<Integer> sectionIds = sections.stream().map(ExamSection::getId).collect(Collectors.toSet());

            List<Results> topicResults = examStudents.stream()
                .flatMap(es -> resultsRepository.findByExamStudentId(es.getId()).stream())
                .filter(r -> sectionIds.contains(r.getExamSection().getId()))
                .collect(Collectors.toList());

            double avg = topicResults.isEmpty()
                ? 0
                : topicResults.stream().mapToInt(Results::getMarksScored).average().orElse(0);

            return TopicStatsDTO.builder()
                .topic(topic.getName())
                .averageScore(avg)
                .build();
        })
        .filter(topic -> topic.getAverageScore() > 0)
        .collect(Collectors.toList());
    }

    private List<DifficultyStatsDTO> getDifficultyStats(List<ExamStatsDTO> attemptedExams) {
        int easySum = attemptedExams.stream().mapToInt(e -> e.getDifficultyBreakdown().getEasy()).sum();
        int mediumSum = attemptedExams.stream().mapToInt(e -> e.getDifficultyBreakdown().getMedium()).sum();
        int hardSum = attemptedExams.stream().mapToInt(e -> e.getDifficultyBreakdown().getHard()).sum();
        int total = easySum + mediumSum + hardSum;

        return Arrays.asList(
            DifficultyStatsDTO.builder().difficulty("Easy").average(total == 0 ? 0 : easySum * 100.0 / total).build(),
            DifficultyStatsDTO.builder().difficulty("Medium").average(total == 0 ? 0 : mediumSum * 100.0 / total).build(),
            DifficultyStatsDTO.builder().difficulty("Hard").average(total == 0 ? 0 : hardSum * 100.0 / total).build()
        );
    }

    private List<UpcomingExamDTO> getUpcomingExams(Integer studentId) {
        LocalDate today = LocalDate.now();
        return examStudentRepository.findByStudentId(studentId).stream()
            .map(ExamStudent::getExam)
            .filter(exam -> exam.getExamDate() != null && !exam.getExamDate().isBefore(today))
            .map(exam -> UpcomingExamDTO.builder()
                .examCode(exam.getCode())
                .name(exam.getName())
                .date(exam.getExamDate().toString())
                .duration(exam.getDuration())
                .totalMarks(exam.getTotalMarks())
                .topic(exam.getName()) // Replace with actual topic if needed
                .build())
            .collect(Collectors.toList());
    }

    private Integer calculateClassRank(Integer studentId) {
        List<UserInfo> allStudents = userInfoRepository.findAll();
        Map<Integer, Integer> studentTotalScore = new HashMap<>();
        Map<Integer, Integer> studentTotalTime = new HashMap<>();

        for (UserInfo student : allStudents) {
            List<ExamStudent> exams = examStudentRepository.findByStudentId(student.getId());
            int totalScore = 0, totalTime = 0;
            for (ExamStudent es : exams) {
                List<Results> results = resultsRepository.findByExamStudentId(es.getId());
                totalScore += results.stream().mapToInt(Results::getMarksScored).sum();
                totalTime += results.stream().mapToInt(r -> r.getTimeTakenInSeconds() != null ? r.getTimeTakenInSeconds().intValue() : 0).sum();
            }
            studentTotalScore.put(student.getId(), totalScore);
            studentTotalTime.put(student.getId(), totalTime);
        }
        List<Integer> rankedStudentIds = studentTotalScore.entrySet().stream()
            .sorted((a, b) -> {
                int cmp = b.getValue().compareTo(a.getValue());
                if (cmp != 0) return cmp;
                return studentTotalTime.get(a.getKey()).compareTo(studentTotalTime.get(b.getKey()));
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return rankedStudentIds.indexOf(studentId) + 1;
    }

    @Override
    public Integer getCurrentStudentId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userInfoRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Student not found"))
            .getId();
    }
}
