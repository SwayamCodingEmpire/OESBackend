package com.cozentus.oes.serviceImpl;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.CodeAndNameDTO;
import com.cozentus.oes.entities.Topic;
import com.cozentus.oes.repositories.TopicRepository;
import com.cozentus.oes.services.TopicService;

import jakarta.validation.Valid;

@Service
public class TopicServiceImpl implements TopicService {
	private final TopicRepository topicRepository;
	
	public TopicServiceImpl(TopicRepository topicRepository) {
		this.topicRepository = topicRepository;
	}
	

	@Override
	public List<CodeAndNameDTO> getAllTopicsDropDown() {
		return topicRepository.findAllByEnabledTrueOrderByNameAsc();
	}


	@Override
	public List<CodeAndNameDTO> getAllTopics() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Transactional
	public void addTopic(@Valid CodeAndNameDTO topicDTO) {
		Topic topic = new Topic(topicDTO);
		topicRepository.save(topic);
		
	}
	
	@Override
	@Transactional
	public void updateTopic(@Valid CodeAndNameDTO topicDTO, String code) {
		Topic existingTopic = topicRepository.findByCode(code)
				.orElseThrow(() -> new IllegalArgumentException("Topic with code " + code + " not found"));
		existingTopic.updateTopic(topicDTO);
		topicRepository.save(existingTopic);
		
	}


	@Override
	public void deleteTopic(String code) {
		int i = topicRepository.deleteByCode(code);
		if(i == 0) {
			throw new IllegalArgumentException("Topic with code " + code + " not found");
		}
	}


	@Override
	public CodeAndNameDTO getTopicByCode(String code) {
		// TODO Auto-generated method stub
		return topicRepository.findByCode(code)
				.map(CodeAndNameDTO::new)
				.orElseThrow(() -> new IllegalArgumentException("Topic with code " + code + " not found"));
	}



}
