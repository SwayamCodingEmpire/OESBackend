package com.cozentus.oes.dto;

import com.cozentus.oes.entities.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    public UserInfoDTO(UserInfo userInfo) {
    	System.out.println("UserInfoDTO constructor called with userInfo: ");
		this.code = userInfo.getCode();
		this.name = userInfo.getName();
		this.email = userInfo.getEmail();
		this.phoneNo = userInfo.getPhoneNo();
	}


}
