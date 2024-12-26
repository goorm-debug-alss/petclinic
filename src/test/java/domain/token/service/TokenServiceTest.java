package domain.token.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.token.Interface.TokenHelperInterface;
import org.springframework.samples.petclinic.domain.token.dto.TokenDto;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.samples.petclinic.domain.token.service.TokenService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TokenService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

	@Mock
	private TokenHelperInterface tokenHelperInterface;

	@InjectMocks
	private TokenService tokenService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@BeforeEach
	void setUp() {
	}

	@Test
	@DisplayName("JWT 토큰 발행 성공")
	void issueToken_Success() {
		// given
		Integer ownerId = 1;
		Map<String, Object> data = new HashMap<>();
		data.put("ownerId", ownerId);

		TokenDto accessToken = new TokenDto("accessToken123", LocalDateTime.now().plusMinutes(15));
		TokenDto refreshToken = new TokenDto("refreshToken123", LocalDateTime.now().plusDays(7));

		when(tokenHelperInterface.issueAccessToken(data)).thenReturn(accessToken);
		when(tokenHelperInterface.issueRefreshToken(data)).thenReturn(refreshToken);

		// when
		TokenResponseDto tokenResponse = tokenService.issueToken(ownerId);

		// then
		assertNotNull(tokenResponse);
		assertEquals("accessToken123", tokenResponse.getAccessToken());
		assertEquals("refreshToken123", tokenResponse.getRefreshToken());
		verify(tokenHelperInterface).issueAccessToken(data);
		verify(tokenHelperInterface).issueRefreshToken(data);
	}

	@Test
	@DisplayName("JWT 토큰 검증 성공")
	void validationToken_Success() {
		// given
		String token = "validToken123";
		Map<String, Object> claims = new HashMap<>();
		claims.put("ownerId", 1);

		when(tokenHelperInterface.validationTokenWithThrow(token)).thenReturn(claims);

		// when
		Integer ownerId = tokenService.validationToken(token);

		// then
		assertNotNull(ownerId);
		assertEquals(1, ownerId);
		verify(tokenHelperInterface).validationTokenWithThrow(token);
	}

	@Test
	@DisplayName("JWT 토큰 검증 실패 - Owner Id가 없을 때")
	void validationToken_Fail_NullOwnerId() {
		// given
		String token = "invalidToken123";
		Map<String, Object> claims = new HashMap<>();

		when(tokenHelperInterface.validationTokenWithThrow(token)).thenReturn(claims);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
			tokenService.validationToken(token));
		assertEquals("ownerId is Null", exception.getMessage());
	}

	@Test
	@DisplayName("HTTP 요청에서 JWT 토큰 추출 성공")
	void extractToken_Success() {
		// given
		when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer validToken123");

		// when
		String token = tokenService.extractToken(httpServletRequest);

		// then
		assertNotNull(token);
		assertEquals("Bearer validToken123", token);
		verify(httpServletRequest).getHeader("Authorization");
	}

	@Test
	@DisplayName("HTTP 요청에서 JWT 토큰 추출 실패 - 헤더가 없을 때")
	void extractToken_Fail_NoHeader() {
		// given
		when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

		// when
		String token = tokenService.extractToken(httpServletRequest);

		// then
		assertNull(token);
		verify(httpServletRequest).getHeader("Authorization");
	}
}
