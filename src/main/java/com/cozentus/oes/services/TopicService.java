package com.cozentus.oes.services;

import java.util.List;

import com.cozentus.oes.dto.CodeAndNameDTO;

import jakarta.validation.Valid;

public interface TopicService {
	List<CodeAndNameDTO> getAllTopicsDropDown();
	
	List<CodeAndNameDTO> getAllTopics();

	void addTopic(@Valid CodeAndNameDTO topicDTO);
	
	
	void updateTopic(@Valid CodeAndNameDTO topicDTO, String code);
	
	void deleteTopic(String code);
	
	CodeAndNameDTO getTopicByCode(String code);
}
