package org.springframework.samples.petclinic.domain.owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.dto.OwnerResponseDto;
import org.springframework.samples.petclinic.domain.owner.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerReadService {

	private final OwnerRepository ownerRepository;

	// 모든 회원 데이터를 조회
	public List<OwnerResponseDto> findAll() {
		return findAllOwners();
	}

	// 특정 회원 조회
	public OwnerResponseDto findById(Integer id) {
		Owner owner = findOwnerByIdOrThrow(id);

		return buildOwnerResponse(owner);
	}

	private List<OwnerResponseDto> findAllOwners() {
		return ownerRepository.findAll().stream()
			.map(owner -> OwnerResponseDto.builder()
				.name(owner.getName())
				.address(owner.getAddress())
				.telephone(owner.getTelephone())
				.city(owner.getCity())
				.build())
			.collect(Collectors.toList());
	}

	private Owner findOwnerByIdOrThrow(Integer id) {
		return ownerRepository.findById(id)
			.orElseThrow(() -> new ApiException(OwnerErrorCode.NO_OWNER));
	}

	private static OwnerResponseDto buildOwnerResponse(Owner owner) {
		return OwnerResponseDto.builder()
			.name(owner.getName())
			.address(owner.getAddress())
			.telephone(owner.getTelephone())
			.city(owner.getCity())
			.build();
	}
}
