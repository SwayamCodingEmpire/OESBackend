package com.cozentus.oes.serviceImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.LoginDTO;
import com.cozentus.oes.dto.LoginResponseDTO;
import com.cozentus.oes.helpers.Roles;
import com.cozentus.oes.services.AuthenticationService;
import com.cozentus.oes.services.JwtService;
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	private final UserDetailsService userDetailsService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	public AuthenticationServiceImpl(UserDetailsService userDetailsService,AuthenticationManager authenticationManager, JwtService jwtService) {
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}
	@Override
	public LoginResponseDTO authenticate(LoginDTO loginDTO) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.email(),loginDTO.password());
		authenticationManager.authenticate(authToken);
		UserDetails userDetails =  userDetailsService.loadUserByUsername(loginDTO.email());
        if (userDetails != null) {
            String jwtToken = jwtService.generateToken(userDetails);

            // Extract role from authorities
            String roleStr = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No role assigned"))
                .getAuthority();

            // Convert string to enum
            Roles role = Roles.valueOf(roleStr.substring(5));

            LoginResponseDTO loginResponse = new LoginResponseDTO(jwtToken, role);
            return loginResponse;
        } else {
            throw new IllegalStateException("User is Invalid");
        }
	}

}
