package com.cozentus.oes.serviceImpl;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.QuestionDTO;
import com.cozentus.oes.dto.SectionDTO;
import com.cozentus.oes.dto.SectionDetailDTO;
import com.cozentus.oes.entities.ExamQuestion;
import com.cozentus.oes.entities.ExamSection;
import com.cozentus.oes.repositories.ExamQuestionRepository;
import com.cozentus.oes.repositories.ExamSectionRepository;
import com.cozentus.oes.services.ExamSectionService;

import lombok.RequiredArgsConstructor;

@Service
public class ExamSectionServiceImpl implements ExamSectionService {
  
  private final ExamSectionRepository  sectionRepo;
  private final ExamQuestionRepository questionRepo;
  
  public ExamSectionServiceImpl(ExamSectionRepository sectionRepo, ExamQuestionRepository questionRepo) {
	this.sectionRepo = sectionRepo;
	this.questionRepo = questionRepo;
  }

  @Override
  public List<SectionDetailDTO> getSectionsByExamCode(String examCode) {
    // 1️⃣ load sections
    List<ExamSection> sections = sectionRepo.findByExamCode(examCode);

    // 2️⃣ load all exam→question links
    List<ExamQuestion> links = questionRepo.findByExam_Code(examCode);

    // 3️⃣ group links by the section id (which is topic.id in your model)
    Map<Integer, List<ExamQuestion>> bySection = links.stream()
      .collect(Collectors.groupingBy(eq -> eq.getQuestion()
                                             .getTopic()
                                             .getId()));

    // 4️⃣ map each section into SectionDetailDTO
    return sections.stream()
      .map(sec -> {
        List<QuestionDTO> qDtos = bySection
          .getOrDefault(sec.getSection().getId(), List.of())
          .stream()
          .map(eq -> {
            var q = eq.getQuestion();
            return new QuestionDTO(
              q.getCode(),
              q.getQuestion(),
              q.getOptionA(),
              q.getOptionB(),
              q.getOptionC(),
              q.getOptionD()
            );
          })
          .toList();

        return new SectionDetailDTO(
          sec.getSection().getId(),
          sec.getSection().getCode(),
          sec.getSection().getName(),
          sec.getDuration(),
          sec.getTotalMarks(),
          qDtos
        );
      })
      .toList();
  }
}

