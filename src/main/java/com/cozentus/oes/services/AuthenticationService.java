package com.cozentus.oes.services;

import org.apache.commons.lang3.tuple.Pair;

import com.cozentus.oes.dto.LoginDTO;
import com.cozentus.oes.dto.LoginResponseDTO;

public interface AuthenticationService {
	LoginResponseDTO authenticate(LoginDTO loginDTO);
	
	Pair<Integer, String> getCurrentUserDetails();

}
