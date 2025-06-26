package com.cozentus.oes;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.cozentus.oes.entities.Credentials;
import com.cozentus.oes.helpers.Roles;
import com.cozentus.oes.repositories.CredentialsRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OesApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	  @MockBean
	    private CredentialsRepository credentialsRepository;
	
	
	  @BeforeEach
	    void testLoginWithMockedUser() throws Exception {
		  Credentials mockUser = new Credentials();
	        mockUser.setEmail("tripathyamitabh1@gmail.com");
	        mockUser.setPassword(passwordEncoder.encode("dkd@cozentus"));
	        mockUser.setRole(Roles.ADMIN);

	        when(credentialsRepository.findByEmail("tripathyamitabh1@gmail.com"))
	            .thenReturn(Optional.of(mockUser));
	        String loginJson = """
	            {
	                "email": "tripathyamitabh1@gmail.com",
	                "password": "dkd@cozentus"
	            }
	        """;

	        mockMvc.perform(post("/v1/login")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(loginJson))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.token").exists())
	            .andExpect(jsonPath("$.role").value("ADMIN"));
	    }

	@Test
	void contextLoads() {
		SecurityContextHolder.getContext().getAuthentication().getDetails();
		System.out.println("Likun");
	}

}
