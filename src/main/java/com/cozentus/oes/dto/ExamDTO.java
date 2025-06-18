package com.cozentus.oes.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ExamDTO {
    private String code;
    private String name;
    private LocalDate examDate;
    private LocalTime examTime;

}

