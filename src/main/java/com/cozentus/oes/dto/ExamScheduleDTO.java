package com.cozentus.oes.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExamScheduleDTO {

	private Integer id;
    private String code;
    private String title;
    private LocalDate date;
    private LocalTime time;
    private Integer duration;
    private Long students;
    private Long results; // NEW
    private String status;
    
}
