package com.cozentus.oes.services;

import java.util.List;

import com.cozentus.oes.dto.SectionDTO;
import com.cozentus.oes.dto.SectionDetailDTO;

public interface ExamSectionService {

	List<SectionDetailDTO> getSectionsByExamCode(String examCode);

}
