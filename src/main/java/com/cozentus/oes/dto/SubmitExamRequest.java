// src/main/java/com/cozentus/oes/dto/SubmitExamRequest.java
package com.cozentus.oes.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

public record SubmitExamRequest(
    @NotEmpty List<AnswerDTO> answers
) {}
