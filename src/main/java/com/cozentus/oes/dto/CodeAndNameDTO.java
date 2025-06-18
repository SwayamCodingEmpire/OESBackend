package com.cozentus.oes.dto;

import com.cozentus.oes.entities.Topic;

public record CodeAndNameDTO(
		String code,
		String name
		) {
	
	public CodeAndNameDTO(Topic topic) {
		this(topic.getCode(), topic.getName());
	}

}
