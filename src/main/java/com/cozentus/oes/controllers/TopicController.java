package com.cozentus.oes.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cozentus.oes.dto.CodeAndNameDTO;
import com.cozentus.oes.services.TopicService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/topic")
public class TopicController {
	private final TopicService topicService;

	public TopicController(TopicService topicService) {
		this.topicService = topicService;
	}

	@GetMapping("/all")
	public ResponseEntity<List<CodeAndNameDTO>> getAllTopicsDropDown() {
		return ResponseEntity.ok(topicService.getAllTopicsDropDown());
	}

	@PostMapping
	public ResponseEntity<String> addTopic(@RequestBody @Valid CodeAndNameDTO codeAndNameDTO) {
		topicService.addTopic(codeAndNameDTO);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}")
				.buildAndExpand(codeAndNameDTO.code()).toUri();
		return ResponseEntity.created(location).body("Topic added successfully");
	}

	@GetMapping("/{code}")
	public ResponseEntity<CodeAndNameDTO> getTopicByCode(@PathVariable String code) {
		return ResponseEntity.ok(topicService.getTopicByCode(code));
	}

	@PutMapping("/code")
	public ResponseEntity<String> update(@RequestBody @Valid CodeAndNameDTO topicDTO, @PathVariable String code) {
		topicService.updateTopic(topicDTO, code);
		return ResponseEntity.ok("Topic updated successfully");

	}

	@DeleteMapping("/{code}")
	public ResponseEntity<Void> deleteTopic(@PathVariable String code) {
		topicService.deleteTopic(code);
		return ResponseEntity.noContent().build();
	}

}
