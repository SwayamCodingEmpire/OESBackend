package com.cozentus.oes.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {
    private String code;
    private String name;
    private String email;
    private String phoneNo;
}
