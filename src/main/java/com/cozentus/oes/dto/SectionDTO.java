// src/main/java/com/cozentus/oes/dto/SectionDTO.java
package com.cozentus.oes.dto;

public record SectionDTO(
    Long   id,
    String name,
    String code,
    Integer orderIndex,
    Integer duration,     // <— section duration in minutes (or seconds, your choice)
    Integer totalMarks,
    String  topicCode
) {}
