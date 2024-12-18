package org.springframework.samples.petclinic.domain.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.token.Interface.TokenHelperInterface;
import org.springframework.samples.petclinic.domain.token.dto.TokenDto;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenHelperInterface tokenHelperInterface;

	// token 발행
	public TokenResponseDto issueToken(Integer ownerId) {
		var data = new HashMap<String, Object>();
		data.put("ownerId", ownerId);

		// access token 발행
		TokenDto accessToken = tokenHelperInterface.issueAccessToken(data);
		// refresh token 발행
		TokenDto refreshToken = tokenHelperInterface.issueRefreshToken(data);

		return TokenResponseDto.builder()
				.accessToken(accessToken.getToken())
				.accessTokenExpiredAt(accessToken.getExpiresAt())
				.refreshToken(refreshToken.getToken())
				.refreshTokenExpiredAt(refreshToken.getExpiresAt())
				.build();
	}

	// token 검증
	public Integer validationToken(String token) {
		var map = tokenHelperInterface.validationTokenWithThrow(token);

		var userId = map.get("ownerId");

		Objects.requireNonNull(userId, () -> {throw new IllegalArgumentException("ownerId is Null");});

		return Integer.parseInt(userId.toString());
	}
}
