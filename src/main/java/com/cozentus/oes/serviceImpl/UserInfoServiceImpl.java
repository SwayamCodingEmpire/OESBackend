package com.cozentus.oes.serviceImpl;import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.RegisterStudentDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.entities.Credentials;
import com.cozentus.oes.entities.UserInfo;
import com.cozentus.oes.helpers.Roles;
import com.cozentus.oes.repositories.CredentialsRepository;
import com.cozentus.oes.repositories.UserInfoRepository;
import com.cozentus.oes.services.UserInfoService;
import com.cozentus.oes.util.EmailService;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
	private final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

	private final UserInfoRepository userInfoRepository;
	private final CredentialsRepository credentialsRepository;
	private final EmailService emailService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	@Cacheable(value = "userInfoAll", key = "'allusersInfo'")
	public List<UserInfoDTO> getAll() {
		return userInfoRepository.findAllByCredentialsRole(Roles.STUDENT).stream().map(this::convertToDTO).toList();
	}

	@Override
	public UserInfoDTO add(UserInfoDTO userInfoDTO) {
		if (userInfoRepository.existsByCode(userInfoDTO.getCode())) {
			throw new RuntimeException("Student with code already exists");
		}
		UserInfo entity = convertToEntity(userInfoDTO);
		return convertToDTO(userInfoRepository.save(entity));
	}

	@Override
	@Caching(evict = {
			@CacheEvict(value = "userInfoAll", key = "'allusersInfo'"),
		    @CacheEvict(value = "userInfo", key = "#userInfoDTO.email"),
		    @CacheEvict(value = "authCache", key = "#userInfoDTO.email"),
		})
	public UserInfoDTO updateUserInfo(UserInfoDTO userInfoDTO) {
		UserInfo existing = userInfoRepository.findByCode(userInfoDTO.getCode())
				.orElseThrow(() -> new RuntimeException("Student not found"));
		existing.setName(userInfoDTO.getName());
		existing.setEmail(userInfoDTO.getEmail());
		existing.setPhoneNo(userInfoDTO.getPhoneNo());

		credentialsRepository.findByEmail(userInfoDTO.getEmail()).ifPresent(credentials -> {
			credentials.setEmail(userInfoDTO.getEmail()); // Update email in credentials
			credentialsRepository.save(credentials);
		});
		return convertToDTO(userInfoRepository.save(existing));
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "userInfoAll", key = "'allusersInfo'"),
		    @CacheEvict(value = "userInfo", key = "#email"),
		    @CacheEvict(value = "userDetailsCache", key = "#email")
		})
	public void deleteByCode(String email, String code) {
		logger.info("Deleting student with code: {}", code);
		if (!userInfoRepository.existsByCode(code))
			throw new RuntimeException("Student not found");
		else
		{
			logger.info("Deleting student with code: {}", code);
			UserInfo userInfo = userInfoRepository.findByCode(code)
					.orElseThrow(() -> new RuntimeException("Student not found"));
			logger.info("Deleting credentials for student with code: {}", code);
			userInfoRepository.deleteByCode(code);
			credentialsRepository.deleteById(userInfo.getCredentials().getId());
		}
	}

	private UserInfoDTO convertToDTO(UserInfo userInfo) {
		return UserInfoDTO.builder().code(userInfo.getCode()).name(userInfo.getName()).email(userInfo.getEmail()).phoneNo(userInfo.getPhoneNo())
				.build();
	}

	private UserInfo convertToEntity(UserInfoDTO userInfoDTO) {
		return UserInfo.builder().code(userInfoDTO.getCode()).name(userInfoDTO.getName()).email(userInfoDTO.getEmail()).phoneNo(userInfoDTO.getPhoneNo())
				.build();
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "userInfoAll", key = "'allusersInfo'"),
		    @CacheEvict(value = "userInfo", key = "#registerStudentDTO.email"),
		    @CacheEvict(value = "authCache", key = "#registerStudentDTO.email"),
		})
	public UserInfoDTO registerStudent(RegisterStudentDTO registerStudentDTO) {
		if (credentialsRepository.existsByEmail(registerStudentDTO.getEmail())) {
			throw new RuntimeException("Email already exists");
		}
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        String randomPassword = "STUD" + randomPart;

		Credentials credentials = Credentials.builder().email(registerStudentDTO.getEmail())
				.password(bCryptPasswordEncoder.encode(randomPassword)).role(Roles.STUDENT).build();
		Credentials savedCreds = credentialsRepository.save(credentials);

		UserInfo userInfo = UserInfo.builder().code(registerStudentDTO.getCode()).name(registerStudentDTO.getName()).email(registerStudentDTO.getEmail())
				.phoneNo(registerStudentDTO.getPhoneNo()).credentials(savedCreds).build();
		UserInfo saved = userInfoRepository.save(userInfo);

		try {
			emailService.sendVerificationEmail(registerStudentDTO.getEmail(), "Registration Successful",
					"Welcome " + registerStudentDTO.getName() + ", your registration is successful. Your credentials are:\nEmail: "
							+ registerStudentDTO.getEmail() + "\nPassword: " + randomPassword);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return convertToDTO(saved); // <-- Map entity to DTO here!
	}

	@Override
	@Transactional
	@CacheEvict(value = "userInfo", key = "'allusersInfo'")
	public void bulkUpload(List<UserInfoDTO> userInfoDTOList) {
		// Step 1: Prepare lists to collect objects
		List<Credentials> credentialsList = userInfoDTOList.stream().map(user -> Credentials.builder()
				.email(user.getEmail()).password("student@123").role(Roles.STUDENT).build())
				.collect(Collectors.toList());

		// Save all credentials first (to get DB-generated IDs)
		credentialsRepository.saveAll(credentialsList);

		// Build a map for quick lookup by code
		Map<String, Credentials> credentialsMap = credentialsList.stream()
				.collect(Collectors.toMap(Credentials::getEmail, c -> c));

		// Prepare and collect all UserInfo, linking credentials
		List<UserInfo> userInfoList = userInfoDTOList.stream()
				.map(user -> UserInfo.builder().code(user.getCode()).name(user.getName()).email(user.getEmail())
						.phoneNo(user.getPhoneNo()).credentials(credentialsMap.get(user.getCode())).build())
				.collect(Collectors.toList());

		// Save all UserInfo
		userInfoRepository.saveAll(userInfoList);

		// After saving all, send mails
		IntStream.range(0, userInfoList.size()).forEach(i -> {
			Credentials credentials = credentialsList.get(i);
			UserInfo userInfo = userInfoList.get(i);
			try {
				emailService.sendVerificationEmail(credentials.getEmail(), "Registration Successful",
						"Welcome " + userInfo.getName()
								+ ", your registration is successful. Your credentials are:\nCode: "
								+ credentials.getEmail() + "\nPassword: " + credentials.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
