package org.springframework.samples.petclinic.domain.token.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.token.Interface.TokenHelperInterface;
import org.springframework.samples.petclinic.domain.token.dto.TokenDto;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

/**
 * JWT 토큰 발행 및 검증과 관련된 로직을 처리
 */
@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenHelperInterface tokenHelperInterface;

	/**
	 * JWT 토큰을 발행
	 *
	 * @param ownerId 사용자 ID (토큰에 포함될 사용자 정보)
	 * @return Access Token과 Refresh Token 정보를 포함하는 {@link TokenResponseDto}
	 */
	public TokenResponseDto issueToken(Integer ownerId) {
		var data = new HashMap<String, Object>();
		data.put("ownerId", ownerId);

		TokenDto accessToken = tokenHelperInterface.issueAccessToken(data);
		TokenDto refreshToken = tokenHelperInterface.issueRefreshToken(data);

		return TokenResponseDto.builder()
				.accessToken(accessToken.getToken())
				.accessTokenExpiredAt(accessToken.getExpiresAt())
				.refreshToken(refreshToken.getToken())
				.refreshTokenExpiredAt(refreshToken.getExpiresAt())
				.build();
	}

	/**
	 * JWT 토큰 검증
	 *
	 * @param token 검증할 JWT 토큰
	 * @return 사용자 ID
	 */
	public Integer validationToken(String token) {
		var map = tokenHelperInterface.validationTokenWithThrow(token);

		var userId = map.get("ownerId");

		Objects.requireNonNull(userId, () -> {throw new IllegalArgumentException("ownerId is Null");});

		return Integer.parseInt(userId.toString());
	}

	/**
	 * HTTP 요청에서 JWT 토큰을 추출
	 *
	 * @param request HTTP 요청 객체
	 * @return Authorization 헤더에 포함된 토큰 (없으면 null)
	 */
	public String extractToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && !token.isEmpty()) {
			return token;
		}
		return null;
	}
}
