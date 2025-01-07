package org.springframework.samples.petclinic.domain.owner.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.owner.dto.*;
import org.springframework.samples.petclinic.domain.owner.service.BlacklistService;
import org.springframework.samples.petclinic.domain.owner.service.OwnerAuthService;
import org.springframework.samples.petclinic.domain.owner.service.OwnerReadService;
import org.springframework.samples.petclinic.domain.owner.service.OwnerProfileService;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.samples.petclinic.domain.token.service.TokenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/owner")
class OwnerController {

	private final OwnerAuthService ownerAuthService;
	private final OwnerReadService ownerReadService;
	private final OwnerProfileService ownerProfileService;
	private final TokenService tokenService;
	private final BlacklistService blacklistService;

	// 회원가입
	@PostMapping("/register")
	public ResponseEntity<OwnerResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
		OwnerResponseDto responseDto = ownerAuthService.register(registerRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
		TokenResponseDto tokenResponseDto = ownerAuthService.login(loginRequestDto);
		return ResponseEntity.ok(tokenResponseDto);
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		String token = tokenService.extractToken(request);

		if (token == null || token.isEmpty())
			return ResponseEntity.badRequest().body("Token is empty");
		blacklistService.addToBlacklist(token);

		return ResponseEntity.ok("Logged out successfully");
	}

	// 토큰 재발급
	@GetMapping("/tokens")
	public ResponseEntity<TokenResponseDto> tokens() {
		TokenResponseDto tokenResponseDto = ownerAuthService.tokens();
		return ResponseEntity.ok(tokenResponseDto);
	}

	// 회원정보 수정
	@PutMapping("/update/{ownerId}")
	public ResponseEntity<OwnerResponseDto> updateProfile(@PathVariable("ownerId") Integer id, @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
		OwnerResponseDto responseDto = ownerProfileService.updateProfile(id, updateProfileRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 비밀번호 변경
	@PutMapping("/update/password/{ownerId}")
	public ResponseEntity updatePassword(@PathVariable("ownerId") Integer id, @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
		ownerProfileService.updatePassword(id, updatePasswordRequestDto);
		return ResponseEntity.ok().build();
	}

	// 회원 목록 조회
	@GetMapping
	public ResponseEntity<List<OwnerResponseDto>> findAll() {
		return ResponseEntity.ok(ownerReadService.findAll());
	}

	// 특정 회원 조회
	@GetMapping("/{ownerId}")
	public ResponseEntity<OwnerResponseDto> findById(@PathVariable("ownerId") Integer id) {
		return ResponseEntity.ok(ownerReadService.findById(id));
	}
}
