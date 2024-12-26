package domain.owner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.domain.owner.service.BlacklistService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BlacklistService 단위 테스트
 */
class BlacklistServiceTest {

	private BlacklistService blacklistService;

	@BeforeEach
	void setUp() {
		blacklistService = new BlacklistService();
	}

	@Test
	@DisplayName("블랙리스트에 토큰 추가 성공")
	void addToBlacklist_Success() {
		// given
		String token = "testToken123";

		// when
		blacklistService.addToBlacklist(token);

		// then
		assertTrue(blacklistService.isBlacklisted(token), "토큰이 블랙리스트에 포함되어야 합니다.");
	}

	@Test
	@DisplayName("블랙리스트 확인 - 포함된 토큰")
	void isBlacklisted_TokenExists() {
		// given
		String token = "testToken123";
		blacklistService.addToBlacklist(token);

		// when
		boolean result = blacklistService.isBlacklisted(token);

		// then
		assertTrue(result, "블랙리스트에 토큰이 포함되어야 합니다.");
	}

	@Test
	@DisplayName("블랙리스트 확인 - 포함되지 않은 토큰")
	void isBlacklisted_TokenDoesNotExist() {
		// given
		String token = "testToken123";

		// when
		boolean result = blacklistService.isBlacklisted(token);

		// then
		assertFalse(result, "블랙리스트에 토큰이 포함되지 않아야 합니다.");
	}
}
