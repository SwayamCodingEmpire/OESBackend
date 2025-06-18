package com.cozentus.oes.serviceImpl;import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.cozentus.oes.dto.RegisterStudentDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.entities.Credentials;
import com.cozentus.oes.entities.UserInfo;
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

	private final UserInfoRepository userInfoRepository;
	private final CredentialsRepository credentialsRepository;
	private final EmailService emailService;

	@Override
	public List<UserInfoDTO> getAll() {
		return userInfoRepository.findAll().stream().map(this::convertToDTO).toList();
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
	public UserInfoDTO updateUserInfo(UserInfoDTO userInfoDTO) {
		UserInfo existing = userInfoRepository.findByCode(userInfoDTO.getCode())
				.orElseThrow(() -> new RuntimeException("Student not found"));
		existing.setName(userInfoDTO.getName());
		existing.setEmail(userInfoDTO.getEmail());
		existing.setPhoneNo(userInfoDTO.getPhoneNo());

		credentialsRepository.findByCode(userInfoDTO.getCode()).ifPresent(credentials -> {
			credentials.setEmail(userInfoDTO.getEmail()); // Update email in credentials
			credentialsRepository.save(credentials);
		});
		return convertToDTO(userInfoRepository.save(existing));
	}

	@Override
	@Transactional
	public void deleteByCode(String code) {
		if (!userInfoRepository.existsByCode(code))
			throw new RuntimeException("Student not found");
		{
			userInfoRepository.deleteByCode(code);
			credentialsRepository.deleteByCode(code);
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
	public UserInfoDTO registerStudent(RegisterStudentDTO registerStudentDTO) {
		if (credentialsRepository.existsByEmail(registerStudentDTO.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		Credentials credentials = Credentials.builder().code(registerStudentDTO.getCode()).email(registerStudentDTO.getEmail())
				.password("student@123").role("STUDENT").build();
		Credentials savedCreds = credentialsRepository.save(credentials);

		UserInfo userInfo = UserInfo.builder().code(registerStudentDTO.getCode()).name(registerStudentDTO.getName()).email(registerStudentDTO.getEmail())
				.phoneNo(registerStudentDTO.getPhoneNo()).credentials(savedCreds).build();
		UserInfo saved = userInfoRepository.save(userInfo);

		try {
			emailService.sendVerificationEmail(registerStudentDTO.getEmail(), "Registration Successful",
					"Welcome " + registerStudentDTO.getName() + ", your registration is successful. Your credentials are:\nCode: "
							+ registerStudentDTO.getCode() + "\nPassword: student@123");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return convertToDTO(saved); // <-- Map entity to DTO here!
	}

	@Override
	@Transactional
	public void bulkUpload(List<UserInfoDTO> userInfoDTOList) {
		// Step 1: Prepare lists to collect objects
		List<Credentials> credentialsList = userInfoDTOList.stream().map(user -> Credentials.builder()
				.code(user.getCode()).email(user.getEmail()).password("student@123").role("STUDENT").build())
				.collect(Collectors.toList());

		// Save all credentials first (to get DB-generated IDs)
		credentialsRepository.saveAll(credentialsList);

		// Build a map for quick lookup by code
		Map<String, Credentials> credentialsMap = credentialsList.stream()
				.collect(Collectors.toMap(Credentials::getCode, c -> c));

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
								+ credentials.getCode() + "\nPassword: " + credentials.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
