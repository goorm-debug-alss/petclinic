package org.springframework.samples.petclinic.domain.owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.dto.UpdatePasswordRequestDto;
import org.springframework.samples.petclinic.domain.owner.dto.UpdateProfileRequestDto;
import org.springframework.samples.petclinic.domain.owner.exception.InvalidPasswordException;
import org.springframework.samples.petclinic.domain.owner.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.stereotype.Service;

/**
 * 주인 수정 서비스
 * - 주인 정보를 수정, 검증, 저장하고 응답
 */
@Service
@RequiredArgsConstructor
public class OwnerProfileService {

	private final OwnerRepository ownerRepository;

	/**
	 * 회원 프로필 정보 수정
	 *
	 * @param  id 수정할 회원의 고유 ID
	 * @param  updateProfileRequestDto 클라이언트로부터 전달받은 프로필 수정 요청 데이터
	 */
	public void updateProfile(Integer id, UpdateProfileRequestDto updateProfileRequestDto) {
		Owner owner = findOwnerByIdOrThrow(id);

		updateOwnerProfileFields(updateProfileRequestDto, owner);

		ownerRepository.save(owner);
	}

	/**
	 * 회원 비밀번호 수정
	 *
	 * @param id 수정할 회원의 고유 ID
	 * @param updatePasswordRequestDto 클라이언트로부터 전달받은 비밀번호 수정 요청 데이터
	 */
	public void updatePassword(Integer id, UpdatePasswordRequestDto updatePasswordRequestDto) {
		Owner owner = findOwnerByIdOrThrow(id);

		validateCurrentPassword(updatePasswordRequestDto, owner);

		updateOwnerPassword(updatePasswordRequestDto, owner);
		ownerRepository.save(owner);
	}

	private Owner findOwnerByIdOrThrow(Integer id) {
		return ownerRepository.findById(id)
			.orElseThrow(() -> new OwnerNotFoundException("Owner not found with id:" + id));
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

	private static void validateCurrentPassword(UpdatePasswordRequestDto updatePasswordRequestDto, Owner owner) {
		if (!owner.getPassword().equals(updatePasswordRequestDto.getCurrentPassword()))
			throw new InvalidPasswordException("Password is not correct");
	}

	private static void updateOwnerPassword(UpdatePasswordRequestDto updatePasswordRequestDto, Owner owner) {
		owner.updatePassword(updatePasswordRequestDto.getNewPassword());
	}
}
