package org.springframework.samples.petclinic.domain.owner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.owner.dto.LoginRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.RegisterRequestDto;
import org.springframework.samples.petclinic.domain.owner.service.OwnerService;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.web.bind.annotation.*;

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
}
