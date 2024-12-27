package org.springframework.samples.petclinic.domain.owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.dto.LoginRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.RegisterRequestDto;
import org.springframework.samples.petclinic.domain.owner.exception.InvalidPasswordException;
import org.springframework.samples.petclinic.domain.owner.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.samples.petclinic.domain.token.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service
@RequiredArgsConstructor
public class OwnerAuthService {

	private final OwnerRepository ownerRepository;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;

	// 회원가입
	public void register(RegisterRequestDto registerRequestDto) {
		validateOwnerDoesNotExist(registerRequestDto);

		String encryptedPassword = passwordEncoder.encode(registerRequestDto.getPassword());

		Owner owner = registerRequestDto.toEntity();
		owner.setPassword(encryptedPassword);
		ownerRepository.save(owner);
	}

	// 로그인
	public TokenResponseDto login(LoginRequestDto loginRequestDto) {
		Owner owner = findOwnerByOwnerIdOrThrow(loginRequestDto);

		validatePasswordOrThrow(loginRequestDto, owner);

		return tokenService.issueToken(owner.getId());
	}

	// 토큰 재발급
	public TokenResponseDto tokens() {
		var requestContext = RequestContextHolder.getRequestAttributes();
		var ownerId = requestContext.getAttribute("ownerId", RequestAttributes.SCOPE_REQUEST);

		return tokenService.issueToken((Integer) ownerId);
	}

	private void validateOwnerDoesNotExist(RegisterRequestDto registerRequestDto) {
		if (ownerRepository.existsByUserId(registerRequestDto.getUserId()))
			throw new OwnerNotFoundException("Owner already exists with userId " + registerRequestDto.getUserId());

	}

	private Owner findOwnerByOwnerIdOrThrow(LoginRequestDto loginRequestDto) {
		return ownerRepository.findByUserId(loginRequestDto.getUserId())
			.orElseThrow(() -> new OwnerNotFoundException("Owner not found with userId " + loginRequestDto.getUserId()));
	}

	private void validatePasswordOrThrow(LoginRequestDto loginRequestDto, Owner owner) {
		if (!passwordEncoder.matches(loginRequestDto.getPassword(), owner.getPassword()))
			throw new InvalidPasswordException("Password is not correct");
	}
}
