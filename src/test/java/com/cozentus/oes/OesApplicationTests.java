package com.cozentus.oes;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.cozentus.oes.dto.ExamDTO;
import com.cozentus.oes.entities.Credentials;
import com.cozentus.oes.entities.Exam;
import com.cozentus.oes.entities.UserInfo;
import com.cozentus.oes.helpers.Roles;
import com.cozentus.oes.repositories.CredentialsRepository;
import com.cozentus.oes.repositories.ExamRepository;
import com.cozentus.oes.repositories.UserInfoRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class OesApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CredentialsRepository credentialsRepository;
    
    @MockitoBean
    private UserInfoRepository userInfoRepository;
    
    @MockitoBean
    private ExamRepository examRepository;
    
    private static String jwtToken;
    
    @BeforeAll
    static void testLoginWithMockedUser(@Autowired MockMvc mockMvc, 
                                       @Autowired PasswordEncoder passwordEncoder,
                                       @Autowired CredentialsRepository credentialsRepository,
                                       @Autowired UserInfoRepository userInfoRepository) throws Exception {
        Credentials mockCredentials = new Credentials();
        mockCredentials.setEmail("tripathyamitabh1@gmail.com");
        mockCredentials.setPassword(passwordEncoder.encode("dkd@cozentus"));
        mockCredentials.setRole(Roles.ADMIN);
        mockCredentials.setEnabled(true);
        mockCredentials.setId(1);
        mockCredentials.setCreatedAt(LocalDateTime.now());
        
        UserInfo mockUserInfo = new UserInfo();
        mockUserInfo.setId(1);
        mockUserInfo.setCode("U0001");
        mockUserInfo.setName("Amitabh Tripathy");
        mockUserInfo.setEmail("tripathyamitabh1@gmail.com");
        mockUserInfo.setAge(28);
        mockUserInfo.setCountry("India");
        mockUserInfo.setState("State0");
        mockUserInfo.setCity("City0");
        mockUserInfo.setAddress("Address 1");
        mockUserInfo.setPinCode("100000");
        mockUserInfo.setPhoneNo("NA");
        mockUserInfo.setEnabled(true);
        mockUserInfo.setCredentials(mockCredentials);

        when(credentialsRepository.findByEmailAndEnabledTrue("tripathyamitabh1@gmail.com"))
            .thenReturn(Optional.of(mockCredentials));
        
        when(userInfoRepository.findByEmailAndEnabledTrue("tripathyamitabh1@gmail.com"))
            .thenReturn(Optional.of(mockUserInfo));
        
        String loginJson = """
            {
                "email": "tripathyamitabh1@gmail.com",
                "password": "dkd@cozentus"
            }
        """;

        MvcResult result = mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andReturn();
        String response = result.getResponse().getContentAsString();
        jwtToken = JsonPath.read(response, "$.token");
    }


    
    @Test
    void testCreateExams() throws Exception {
    	ExamDTO examDto = new ExamDTO(
			"EX123", 
			"Final Exam", 
			LocalDateTime.parse("2025-06-30T00:00:00").toLocalDate(), 
			LocalDateTime.parse("2025-06-30T10:00:00").toLocalTime()
			);
    	 Exam exam = new Exam();
         exam.setCode(examDto.code());
         exam.setName(examDto.name());
         exam.setExamDate(examDto.examDate());
         exam.setExamTime(examDto.examTime());
		
		when(examRepository.save(exam)).thenReturn(new Exam(
				1,
				examDto.code(),
				examDto.name(),
				examDto.examDate(),
				examDto.examTime(),
				0,
				0,
				true,
				LocalDateTime.now(),
				"ADMIN"
				));
    	String validJSON = """
    			{
    			  "code": "EX123",
    			  "name": "Final Exam",
    			  "examDate": "2025-06-30",
    			  "examTime": "10:00:00"
    			}
    			""";
    	mockMvc.perform(post("/v1/exams")
    	        .header("Authorization", "Bearer " + jwtToken)
    	        .contentType(MediaType.APPLICATION_JSON)
    	        .content(validJSON))
    	    .andExpect(status().isOk())
    	    .andExpect(jsonPath("$.code").value("EX123"))
    	    .andExpect(jsonPath("$.name").value("Final Exam"))
    	    .andExpect(jsonPath("$.examDate").value("2025-06-30"))
    	    .andExpect(jsonPath("$.examTime").value("10:00:00"));
    	
    	String inValidJSON = """
    			{
    			  "code": "",
    			  "name": "Final Exam",
    			  "examDate": "2025-06-30",
    			  "examTime": "10:00:00"
    			}
    			""";
    	mockMvc.perform(post("/v1/exams")
    	        .header("Authorization", "Bearer " + jwtToken)
    	        .contentType(MediaType.APPLICATION_JSON)
    	        .content(validJSON))
    	    .andExpect(status().isOk())
    	    .andExpect(jsonPath("$.code").value("EX123"))
    	    .andExpect(jsonPath("$.name").value("Final Exam"))
    	    .andExpect(jsonPath("$.examDate").value("2025-06-30"))
    	    .andExpect(jsonPath("$.examTime").value("10:00:00"));
    	
    }
}
