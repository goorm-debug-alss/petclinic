package org.springframework.samples.petclinic.domain.owner.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BlacklistService {

	private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

	// 토큰을 블랙리스트에 추가
	public void addToBlacklist(String token) {
		blacklist.add(token);
	}

	//토큰이 블랙리스트에 포함되었는지 확인
	public boolean isBlacklisted(String token) {
		return blacklist.contains(token);
	}
}
