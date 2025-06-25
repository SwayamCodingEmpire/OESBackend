package com.cozentus.oes.serviceImpl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cozentus.oes.entities.Credentials;
import com.cozentus.oes.repositories.CredentialsRepository;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final CredentialsRepository credentialsRepository;
	
	public UserDetailsServiceImpl(CredentialsRepository credentialsRepository) {
		this.credentialsRepository = credentialsRepository;
	}
	
	@Override
	@Cacheable(value = "authCache", key = "#email")
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Credentials credentials = credentialsRepository.findByEmail(email)
				.orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + email));
		
		return new User(
				credentials.getEmail(), 
				credentials.getPassword(), 
				credentials.getEnabled(),
				credentials.getEnabled(),
				credentials.getEnabled(),
				credentials.getEnabled(),
				List.of(new SimpleGrantedAuthority("ROLE_" + credentials.getRole().name()))
				);
	}

}
