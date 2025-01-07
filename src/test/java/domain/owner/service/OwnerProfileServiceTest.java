package domain.owner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.dto.UpdatePasswordRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.UpdateProfileRequestDto;
import org.springframework.samples.petclinic.domain.owner.exception.InvalidPasswordException;
import org.springframework.samples.petclinic.domain.owner.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.owner.mapper.OwnerMapper;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.owner.service.OwnerProfileService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OwnerProfileService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class OwnerProfileServiceTest {

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private OwnerMapper ownerMapper;

	@InjectMocks
	private OwnerProfileService ownerProfileService;

	private Owner owner;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setId(1);
		owner.setName("구름");
		owner.setPassword("encryptedPassword");
		owner.setAddress("강남");
		owner.setCity("서울");
		owner.setTelephone("1234567890");
	}

	@Test
	@DisplayName("회원 프로필 수정 성공")
	void updateProfile_Success() {
		// given
		UpdateProfileRequestDto updateProfileRequestDto = new UpdateProfileRequestDto("구름", "해운대", "부산", "0987654321");
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

		// when
		ownerProfileService.updateProfile(1, updateProfileRequestDto);

		// then
		verify(ownerRepository).save(owner);
		assertEquals("구름", owner.getName());
		assertEquals("해운대", owner.getAddress());
		assertEquals("부산", owner.getCity());
		assertEquals("0987654321", owner.getTelephone());
	}

	@Test
	@DisplayName("회원 프로필 수정 실패 - Owner Id가 없을 때")
	void updateProfile_OwnerNotFound() {
		// given
		UpdateProfileRequestDto updateProfileRequestDto = new UpdateProfileRequestDto("구름", "강릉", "강원도", "0987654321");
		when(ownerRepository.findById(1)).thenThrow(new ApiException(OwnerErrorCode.NO_OWNER));

		// when & then
		assertThrows(ApiException.class, () -> ownerProfileService.updateProfile(1, updateProfileRequestDto));
	}

	@Test
	@DisplayName("비밀번호 수정 성공")
	void updatePassword_Success() {
		// given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto("oldPassword", "newPassword");
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
		when(passwordEncoder.matches("oldPassword", "encryptedPassword")).thenReturn(true);
		when(passwordEncoder.encode("newPassword")).thenReturn("newEncryptedPassword");

		// when
		ownerProfileService.updatePassword(1, updatePasswordRequestDto);

		// then
		verify(ownerRepository).save(owner);
		assertEquals("newEncryptedPassword", owner.getPassword());
	}

	@Test
	@DisplayName("비밀번호 수정 실패 - 기존 비밀번호 불일치할 때")
	void updatePassword_InvalidCurrentPassword() {
		// given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto("wrongPassword", "newPassword");
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
		when(passwordEncoder.matches("wrongPassword", "encryptedPassword")).thenThrow(new ApiException(OwnerErrorCode.INVALID_PASSWORD));

		// when & then
		assertThrows(ApiException.class, () -> ownerProfileService.updatePassword(1, updatePasswordRequestDto));
	}

	@Test
	@DisplayName("비밀번호 수정 실패 - Owner Id가 없을 때")
	void updatePassword_OwnerNotFound() {
		// given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto("oldPassword", "newPassword");
		when(ownerRepository.findById(1)).thenThrow(new ApiException(OwnerErrorCode.NO_OWNER));

		// when & then
		assertThrows(ApiException.class, () -> ownerProfileService.updatePassword(1, updatePasswordRequestDto));
	}
}
