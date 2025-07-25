package com.cozentus.oes.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.entities.QuestionBank;

public interface QuestionBankService {
		/**
	 * Adds a new question to the question bank.
	 *
	 * @param question the question to be added
	 * @return the added question
	 */
	void addQuestion(QuestionBankDTO questionBankDTO);

	/**
	 * Retrieves a question by its ID.
	 *
	 * @param id the ID of the question
	 * @return the question with the specified ID, or null if not found
	 */
	QuestionBankDTO getQuestionById(String code);

	/**
	 * Retrieves all questions in the question bank.
	 *
	 * @return a list of all questions
	 */
	List<QuestionBankDTO> getAllQuestionsPageable(Pageable pageable);
	
	List<QuestionBankDTO> getAllQuestions();

	/**
	 * Deletes a question by its ID.
	 *
	 * @param id the ID of the question to be deleted
	 */
	void deleteQuestion(String code);
	
	void updateQuestion(QuestionBankDTO questionBankDTO, String code);
	
	void bulkInsertQuestions(List<QuestionBankDTO> questionBankDTOs, String topicCode);
	
	
}
