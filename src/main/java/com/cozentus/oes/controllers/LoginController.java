package com.cozentus.oes.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.oes.dto.LoginDTO;
import com.cozentus.oes.dto.LoginResponseDTO;
import com.cozentus.oes.helpers.Roles;
import com.cozentus.oes.serviceImpl.AuthenticationServiceImpl;
import com.cozentus.oes.services.JwtService;

@RestController
@RequestMapping("/v1/login")
public class LoginController {
	private final AuthenticationManager authenticationManager;
	private final AuthenticationServiceImpl authenticationService;


    public LoginController(AuthenticationManager authenticationManager, AuthenticationServiceImpl authenticationServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationServiceImpl;

    }
	
    @PostMapping
    public ResponseEntity<LoginResponseDTO> loginSuperAdmin(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authenticationService.authenticate(loginDTO));

    }

}
