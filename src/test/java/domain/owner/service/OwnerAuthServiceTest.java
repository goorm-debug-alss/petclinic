package domain.owner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.dto.LoginRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.RegisterRequestDto;
import org.springframework.samples.petclinic.domain.owner.exception.InvalidPasswordException;
import org.springframework.samples.petclinic.domain.owner.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.owner.service.OwnerAuthService;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.samples.petclinic.domain.token.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OwnerAuthService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class OwnerAuthServiceTest {

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private TokenService tokenService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private OwnerAuthService ownerAuthService;

	private RegisterRequestDto registerRequestDto;
	private LoginRequestDto loginRequestDto;
	private Owner owner;

	@BeforeEach
	void setUp() {
		registerRequestDto = new RegisterRequestDto("testUser", "password", "Test User", "Address", "City", "1234567890");
		loginRequestDto = new LoginRequestDto("testUser", "password");
		owner = new Owner();
		owner.setId(1);
		owner.setUserId("testUser");
		owner.setPassword("encryptedPassword");
	}

	@Test
	@DisplayName("회원가입 성공")
	void register_Success() {
		// given
		when(ownerRepository.existsByUserId(registerRequestDto.getUserId())).thenReturn(false);
		when(passwordEncoder.encode(registerRequestDto.getPassword())).thenReturn("encryptedPassword");

		// when
		ownerAuthService.register(registerRequestDto);

		// then
		verify(ownerRepository).save(any(Owner.class));
	}

	@Test
	@DisplayName("회원가입 실패 - Owner Id가 이미 존재할 때")
	void register_UserAlreadyExists() {
		// given
		when(ownerRepository.existsByUserId(registerRequestDto.getUserId())).thenReturn(true);

		// when & then
		OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class, () ->
			ownerAuthService.register(registerRequestDto));
		assertEquals("Owner already exists with ownerId testUser", exception.getMessage());
	}

	@Test
	@DisplayName("로그인 성공")
	void login_Success() {
		// given
		when(ownerRepository.findByUserId(loginRequestDto.getUserId())).thenReturn(Optional.of(owner));
		when(passwordEncoder.matches(loginRequestDto.getPassword(), owner.getPassword())).thenReturn(true);
		when(tokenService.issueToken(owner.getId())).thenReturn(new TokenResponseDto("accessToken", null, "refreshToken", null));

		// when
		TokenResponseDto tokenResponseDto = ownerAuthService.login(loginRequestDto);

		// then
		assertNotNull(tokenResponseDto);
		assertEquals("accessToken", tokenResponseDto.getAccessToken());
		assertEquals("refreshToken", tokenResponseDto.getRefreshToken());
	}

	@Test
	@DisplayName("로그인 실패 - Owner ID가 존재하지 않을 때")
	void login_UserNotFound() {
		// given
		when(ownerRepository.findByUserId(loginRequestDto.getUserId())).thenReturn(Optional.empty());

		// when & then
		OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class, () ->
			ownerAuthService.login(loginRequestDto));
		assertEquals("Owner not found with ownerId testUser", exception.getMessage());
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호가 불일치할 때")
	void login_InvalidPassword() {
		// given
		when(ownerRepository.findByUserId(loginRequestDto.getUserId())).thenReturn(Optional.of(owner));
		when(passwordEncoder.matches(loginRequestDto.getPassword(), owner.getPassword())).thenReturn(false);

		// when & then
		InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
			ownerAuthService.login(loginRequestDto));
		assertEquals("Password is not correct", exception.getMessage());
	}

	@Test
	@DisplayName("토큰 재발급 성공")
	void tokens_Success() {
		// given
		RequestAttributes requestAttributes = mock(RequestAttributes.class);
		when(requestAttributes.getAttribute("ownerId", RequestAttributes.SCOPE_REQUEST)).thenReturn(1);
		RequestContextHolder.setRequestAttributes(requestAttributes);
		when(tokenService.issueToken(1)).thenReturn(new TokenResponseDto("accessToken", null, "refreshToken", null));

		// when
		TokenResponseDto tokenResponseDto = ownerAuthService.tokens();

		// then
		assertNotNull(tokenResponseDto);
		assertEquals("accessToken", tokenResponseDto.getAccessToken());
		assertEquals("refreshToken", tokenResponseDto.getRefreshToken());
	}
}
