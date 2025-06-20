package com.cozentus.oes.dto;

import java.util.List;

public record SectionDetailDTO(
	    Integer   sectionId,
	    String    sectionCode,
	    String    sectionName,
	    Integer   duration,     // section.duration
	    Integer   totalMarks,   // section.totalMarks
	    List<QuestionDTO> questions
	) { }