package org.springframework.samples.petclinic.domain.owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.dto.LoginRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.RegisterRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.UpdatePasswordRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.UpdateProfileRequestDto;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.samples.petclinic.domain.token.service.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OwnerService {
	private final OwnerRepository ownerRepository;
	private final TokenService tokenService;

	public void register(RegisterRequestDto registerRequestDto) {
		boolean isExist = ownerRepository.existsByUserId(registerRequestDto.getUserId());

		if(isExist) {
			throw new IllegalArgumentException("Owner already exists with userId: " + registerRequestDto.getUserId());
		}

		Owner owner = registerRequestDto.toEntity();
		ownerRepository.save(owner);
	}

	public TokenResponseDto login(LoginRequestDto loginRequestDto) {
		Owner owner = ownerRepository.findByUserId(loginRequestDto.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with userId: " + loginRequestDto.getUserId()));

		// 비밀번호 검증
		if(!owner.getPassword().equals(loginRequestDto.getPassword())) {
			throw new IllegalArgumentException("Password is not correct");
		}

		// JWT Token 반환
		return tokenService.issueToken(owner.getId());
	}

	public TokenResponseDto tokens() {
		// context에서 ownerId 가져오기
		var requestContext = RequestContextHolder.getRequestAttributes();
		var ownerId = requestContext.getAttribute("ownerId", RequestAttributes.SCOPE_REQUEST);

		// JWT Token 반환
		return tokenService.issueToken((Integer) ownerId);
	}

	public void updateProfile(Integer id, UpdateProfileRequestDto updateProfileRequestDto) {
		Owner owner = ownerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + id));

		Optional.ofNullable(updateProfileRequestDto.getName()).ifPresent(owner::setName);
		Optional.ofNullable(updateProfileRequestDto.getAddress()).ifPresent(owner::setAddress);
		Optional.ofNullable(updateProfileRequestDto.getTelephone()).ifPresent(owner::setTelephone);
		Optional.ofNullable(updateProfileRequestDto.getCity()).ifPresent(owner::setCity);

		ownerRepository.save(owner);
	}

	public void updatePassword(Integer id, UpdatePasswordRequestDto updatePasswordRequestDto) {
		Owner owner = ownerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + id));

		if(!owner.getPassword().equals(updatePasswordRequestDto.getCurrentPassword())) {
			throw new IllegalArgumentException("Password is not correct");
		}

		owner.setPassword(updatePasswordRequestDto.getNewPassword());

		ownerRepository.save(owner);
	}
}
