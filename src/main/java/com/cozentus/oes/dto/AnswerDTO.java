// src/main/java/com/cozentus/oes/dto/AnswerDTO.java
package com.cozentus.oes.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerDTO(
    @NotBlank String questionCode,
    @NotBlank String answer
) {}
