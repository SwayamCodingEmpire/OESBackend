package com.cozentus.oes.serviceImpl;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cozentus.oes.dto.RegisterStudentDTO;
import com.cozentus.oes.dto.UserInfoDTO;
import com.cozentus.oes.entities.Credentials;
import com.cozentus.oes.entities.UserInfo;
import com.cozentus.oes.repositories.CredentialsRepository;
import com.cozentus.oes.repositories.UserInfoRepository;
import com.cozentus.oes.services.UserInfoService;
import com.cozentus.oes.util.EmailService;
import com.opencsv.CSVReader;

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
		return userInfoRepository.findAll().stream().map(this::toDTO).toList();
	}

	@Override
	public UserInfoDTO add(UserInfoDTO dto) {
		if (userInfoRepository.existsByCode(dto.getCode())) {
			throw new RuntimeException("Student with code already exists");
		}
		UserInfo entity = toEntity(dto);
		return toDTO(userInfoRepository.save(entity));
	}

	@Override
	public UserInfoDTO update(UserInfoDTO dto) {
		UserInfo existing = userInfoRepository.findByCode(dto.getCode())
				.orElseThrow(() -> new RuntimeException("Student not found"));
		existing.setName(dto.getName());
		existing.setEmail(dto.getEmail());
		existing.setPhoneNo(dto.getPhoneNo());

		credentialsRepository.findByCode(dto.getCode()).ifPresent(credentials -> {
			credentials.setEmail(dto.getEmail()); // Update email in credentials
			credentialsRepository.save(credentials);
		});
		return toDTO(userInfoRepository.save(existing));
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

	private UserInfoDTO toDTO(UserInfo e) {
		return UserInfoDTO.builder().code(e.getCode()).name(e.getName()).email(e.getEmail()).phoneNo(e.getPhoneNo())
				.build();
	}

	private UserInfo toEntity(UserInfoDTO d) {
		return UserInfo.builder().code(d.getCode()).name(d.getName()).email(d.getEmail()).phoneNo(d.getPhoneNo())
				.build();
	}

	@Override
	@Transactional
	public UserInfoDTO registerStudent(RegisterStudentDTO dto) {
		if (credentialsRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		Credentials credentials = Credentials.builder().code(dto.getCode()).email(dto.getEmail())
				.password("student@123").role("STUDENT").build();
		Credentials savedCreds = credentialsRepository.save(credentials);

		UserInfo userInfo = UserInfo.builder().code(dto.getCode()).name(dto.getName()).email(dto.getEmail())
				.phoneNo(dto.getPhoneNo()).credentials(savedCreds).build();
		UserInfo saved = userInfoRepository.save(userInfo);

		try {
			emailService.sendVerificationEmail(dto.getEmail(), "Registration Successful",
					"Welcome " + dto.getName() + ", your registration is successful. Your credentials are:\nCode: "
							+ dto.getCode() + "\nPassword: student@123");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return toDTO(saved); // <-- Map entity to DTO here!
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
