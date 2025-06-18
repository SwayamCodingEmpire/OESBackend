package com.cozentus.oes.dto;

import lombok.Data;

@Data
public class RegisterStudentDTO {
    private String code;
    private String name;
    private String email;
    private String password;
    private String phoneNo;
}
