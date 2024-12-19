package org.springframework.samples.petclinic.domain.owner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.owner.dto.*;
import org.springframework.samples.petclinic.domain.owner.service.OwnerService;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/owner")
class OwnerController {
	private final OwnerService ownerService;

	// 회원가입
	@PostMapping("/register")
	public ResponseEntity register(@RequestBody RegisterRequestDto registerRequestDto) {
		ownerService.register(registerRequestDto);
		return ResponseEntity.ok().build();
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
		TokenResponseDto tokenResponseDto = ownerService.login(loginRequestDto);
		return ResponseEntity.ok(tokenResponseDto);
	}

	// 토큰 재발급
	@GetMapping("/tokens")
	public ResponseEntity<TokenResponseDto> tokens() {
		TokenResponseDto tokenResponseDto = ownerService.tokens();
		return ResponseEntity.ok(tokenResponseDto);
	}

	// 정보 수정
	@PutMapping("/update/{id}")
	public ResponseEntity updateProfile(@PathVariable Integer id, @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
		ownerService.updateProfile(id, updateProfileRequestDto);
		return ResponseEntity.ok().build();
	}

	// 비밀번호 변경
	@PutMapping("/update/password/{id}")
	public ResponseEntity updatePassword(@PathVariable Integer id, @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
		ownerService.updatePassword(id, updatePasswordRequestDto);
		return ResponseEntity.ok().build();
	}

	// 목록 조회
	@GetMapping
	public ResponseEntity<List<OwnerResponseDto>> findAll() {
		return ResponseEntity.ok(ownerService.findAll());
	}
}
