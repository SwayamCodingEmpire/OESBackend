package com.cozentus.oes.services;

import com.cozentus.oes.dto.LoginDTO;
import com.cozentus.oes.dto.LoginResponseDTO;

public interface AuthenticationService {
	LoginResponseDTO authenticate(LoginDTO loginDTO);

}
