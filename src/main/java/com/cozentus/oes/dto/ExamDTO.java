package com.cozentus.oes.dto;



import java.time.LocalDate;
import java.time.LocalTime;


public record ExamDTO (
    String code,
    String name,
    LocalDate examDate,
    LocalTime examTime)
{

}

