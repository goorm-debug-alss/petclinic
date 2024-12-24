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

/**
 * 주인 관련 요청을 처리하는 컨트롤러
 * - 이 클래스는 회원가입 및 로그인, 토큰 발급, 조회 및 수정 요청을 처리하며, 서비스 계층과 연결되어 비즈니스 로직을 수행
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/owner")
class OwnerController {

	private final OwnerAuthService ownerAuthService;
	private final OwnerReadService ownerReadService;
	private final OwnerProfileService ownerProfileService;
	private final TokenService tokenService;
	private final BlacklistService blacklistService;

	/**
	 * 회원가입 API
	 *
	 * @param registerRequestDto 클라이언트로부터 전달받은 회원가입 요청 데이터
	 * @return 성공 시 상태 코드 200 OK 반환
	 */
	@PostMapping("/register")
	public ResponseEntity register(@RequestBody RegisterRequestDto registerRequestDto) {
		ownerAuthService.register(registerRequestDto);
		return ResponseEntity.ok().build();
	}

	/**
	 * 로그인 API
	 *
	 * @param loginRequestDto 클라이언트로부터 전달받은 로그인 요청 데이터
	 * @return 인증 토큰을 포함하는 응답 DTO 반환
	 */
	@PostMapping("/login")
	public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
		TokenResponseDto tokenResponseDto = ownerAuthService.login(loginRequestDto);
		return ResponseEntity.ok(tokenResponseDto);
	}

	/**
	 * 로그아웃 API
	 *
	 * @param request Http 요청 객체
	 * @return 로그아웃 처리 결과
	 */
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		String token = tokenService.extractToken(request);

		if (token == null || token.isEmpty())
			return ResponseEntity.badRequest().body("Token is empty");
		blacklistService.addToBlacklist(token);

		return ResponseEntity.ok("Logged out successfully");
	}

	/**
	 * 토큰 재발급 API
	 *
	 * @return 재발급된 인증 토큰을 포함하는 응답 DTO 반환
	 */
	@GetMapping("/tokens")
	public ResponseEntity<TokenResponseDto> tokens() {
		TokenResponseDto tokenResponseDto = ownerAuthService.tokens();
		return ResponseEntity.ok(tokenResponseDto);
	}

	/**
	 * 회원 정보 수정 API
	 *
	 * @param id 수정할 회원의 고유 ID
	 * @param updateProfileRequestDto 클라리언트로부터 전달받은 회원 정보 수정 요청 데이터
	 * @return 성공 시 상태 코드 200 OK 반환
	 */
	@PutMapping("/update/{ownerId}")
	public ResponseEntity updateProfile(@PathVariable("ownerId") Integer id, @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
		ownerProfileService.updateProfile(id, updateProfileRequestDto);
		return ResponseEntity.ok().build();
	}

	/**
	 * 비밀번호 변경 API
	 *
	 * @param id 변경할 회원의 고유 ID
	 * @param updatePasswordRequestDto 클라이언트로부터 전달받은 비밀번호 변경 요청 데이터
	 * @return 성공 시 상태 코드 200 OK 반환
	 */
	@PutMapping("/update/password/{ownerId}")
	public ResponseEntity updatePassword(@PathVariable("ownerId") Integer id, @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
		ownerProfileService.updatePassword(id, updatePasswordRequestDto);
		return ResponseEntity.ok().build();
	}

	/**
	 * 회원 목록 조회 API
	 *
	 * @return 모든 회원 정보를 포함하는 응답 DTO 목록 반환
	 */
	@GetMapping
	public ResponseEntity<List<OwnerResponseDto>> findAll() {
		return ResponseEntity.ok(ownerReadService.findAll());
	}

	/**
	 * 특정 회원 조회 API
	 *
	 * @param id 조회할 회원의 고유 ID
	 * @return 해당 회원의 상세 정보를 포함하는 응답 DTO 반환
	 */
	@GetMapping("/{ownerId}")
	public ResponseEntity<OwnerResponseDto> findById(@PathVariable("ownerId") Integer id) {
		return ResponseEntity.ok(ownerReadService.findById(id));
	}
}
