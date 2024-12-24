package org.springframework.samples.petclinic.domain.owner.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT 토큰 블랙리스트를 관리*/
@Service
public class BlacklistService {

	private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

	/**
	 * 토큰을 블랙리스트에 추가
	 *
	 * @param token 블랙리스트에 추가할 JWT 토큰
	 */
	public void addToBlacklist(String token) {
		blacklist.add(token);
	}

	/**
	 * 토큰이 블랙리스트에 포함되었는지 확인
	 *
	 * @param token 확인할 토큰
	 * @return 포함 여부
	 */
	public boolean isBlacklisted(String token) {
		return blacklist.contains(token);
	}
}
