package org.springframework.samples.petclinic.domain.token.Interface;

import org.springframework.samples.petclinic.domain.token.dto.TokenDto;

import java.util.Map;

public interface TokenHelperInterface {
	TokenDto issueAccessToken(Map<String, Object> data);

	TokenDto issueRefreshToken(Map<String, Object> data);

	Map<String, Object> validationTokenWithThrow(String token);
}
