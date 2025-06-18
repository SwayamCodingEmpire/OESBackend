package com.cozentus.oes.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.entities.QuestionBank;
import com.cozentus.oes.entities.Topic;
import com.cozentus.oes.exceptions.ResourceNotFoundException;
import com.cozentus.oes.repositories.QuestionBankRepository;
import com.cozentus.oes.repositories.TopicRepository;
import com.cozentus.oes.services.QuestionBankService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
@Service
public class QuestionBankServiceImpl implements QuestionBankService {
	@PersistenceContext
	private EntityManager entityManager;
	private final QuestionBankRepository questionBankRepository;
	private final TopicRepository topicRepository;
	
	public QuestionBankServiceImpl(QuestionBankRepository questionBankRepository, TopicRepository topicRepository) {
		this.questionBankRepository = questionBankRepository;
		this.topicRepository = topicRepository;
	}

	@Override
	@Transactional
	public void addQuestion(QuestionBankDTO questionBankDTO) {
		// TODO Auto-generated method stub
		QuestionBank questionBank = new QuestionBank(questionBankDTO);
		Topic topic = topicRepository.findByCode(questionBankDTO.topicCode()).orElseThrow(()-> new ResourceNotFoundException("Invalid Topics"));;
		questionBank.setTopic(topic);
		questionBankRepository.save(questionBank);
	}

	@Override
	public QuestionBankDTO getQuestionById(String code) {
		// TODO Auto-generated method stub
		return questionBankRepository.findByCode(code)
				.map(QuestionBankDTO::new)
				.orElseThrow(() -> new ResourceNotFoundException("Question with id " + code + " not found"));
	}

	@Override
	public List<QuestionBankDTO> getAllQuestionsPageable(Pageable pageable) {
	    List<QuestionBank> questionBanks = questionBankRepository.findAllByEnabledTrue(pageable);
	    return questionBanks.stream()
	            .map(QuestionBankDTO::new)
	            .toList();
	}

	@Override
	public void deleteQuestion(String code) {
		int noOfDeleted = questionBankRepository.softDeleteByCode(code);
		if(noOfDeleted == 0) {
			throw new ResourceNotFoundException("Question with code " + code + " not found");
		}
	}

	@Override
	@Transactional
	public void updateQuestion(QuestionBankDTO questionBankDTO, String code) {
		QuestionBank existingQuestionBank = questionBankRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Question with code " + questionBankDTO.code() + " not found"));
		existingQuestionBank.updateQuestionFromDTO(questionBankDTO);
		Topic topic = topicRepository.findByCode(questionBankDTO.topicCode()).orElseThrow(()-> new ResourceNotFoundException("Invalid Topics"));;
		existingQuestionBank.setTopic(topic);
		questionBankRepository.save(existingQuestionBank);
	}

	@Override
	public List<QuestionBankDTO> getAllQuestions() {
		// TODO Auto-generated method stub
	    List<QuestionBank> questionBanks = questionBankRepository.findAllByEnabledTrue();
	    return questionBanks.stream()
	            .map(QuestionBankDTO::new)
	            .toList();
	}
	
	public void bulkInsertQuestions(List<QuestionBankDTO> questionBankDTOs, String topicCode) {
		List<QuestionBank> questionBanks = questionBankDTOs.stream()
				.map(QuestionBank::new)
				.collect(Collectors.toList());
		

			Topic topic = topicRepository.findByCode(topicCode)
					.orElseThrow(() -> new ResourceNotFoundException("Invalid Topic: " + topicCode));

		
		questionBankRepository.saveAll(questionBanks);
	}

}
