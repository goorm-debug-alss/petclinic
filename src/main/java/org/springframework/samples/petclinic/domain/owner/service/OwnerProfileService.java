package org.springframework.samples.petclinic.domain.owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.dto.OwnerResponseDto;
import org.springframework.samples.petclinic.domain.owner.dto.UpdatePasswordRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.UpdateProfileRequestDto;
import org.springframework.samples.petclinic.domain.owner.mapper.OwnerMapper;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerProfileService {

	private final OwnerRepository ownerRepository;
	private final PasswordEncoder passwordEncoder;
	private final OwnerMapper ownerMapper;

	// 회원 프로필 수정
	public OwnerResponseDto updateProfile(Integer id, UpdateProfileRequestDto updateProfileRequestDto) {
		Owner owner = findOwnerByIdOrThrow(id);

		updateOwnerProfileFields(updateProfileRequestDto, owner);

		ownerRepository.save(owner);
		return ownerMapper.toDto(owner);
	}

	// 회원 비밀번호 변경
	public void updatePassword(Integer id, UpdatePasswordRequestDto updatePasswordRequestDto) {
		Owner owner = findOwnerByIdOrThrow(id);

		validateCurrentPassword(updatePasswordRequestDto, owner);

		updateOwnerPassword(updatePasswordRequestDto, owner);
		ownerRepository.save(owner);
	}

	private Owner findOwnerByIdOrThrow(Integer id) {
		return ownerRepository.findById(id)
			.orElseThrow(() -> new ApiException(OwnerErrorCode.NO_OWNER));
	}

	private static void updateOwnerProfileFields(UpdateProfileRequestDto updateProfileRequestDto, Owner owner) {
		if (updateProfileRequestDto.getName() != null)
			owner.updateName(updateProfileRequestDto.getName());
		if (updateProfileRequestDto.getAddress() != null)
			owner.updateAddress(updateProfileRequestDto.getAddress());
		if (updateProfileRequestDto.getTelephone() != null)
			owner.updateTelephone(updateProfileRequestDto.getTelephone());
		if (updateProfileRequestDto.getCity() != null)
			owner.updateCity(updateProfileRequestDto.getCity());
	}

	private void validateCurrentPassword(UpdatePasswordRequestDto updatePasswordRequestDto, Owner owner) {
		if (!passwordEncoder.matches(updatePasswordRequestDto.getCurrentPassword(), owner.getPassword()))
			throw new ApiException(OwnerErrorCode.NO_OWNER);
	}

	private void updateOwnerPassword(UpdatePasswordRequestDto updatePasswordRequestDto, Owner owner) {
		String encryptedPassword = passwordEncoder.encode(updatePasswordRequestDto.getNewPassword());
		owner.updatePassword(encryptedPassword);
	}
}
