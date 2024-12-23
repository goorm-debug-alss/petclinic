package org.springframework.samples.petclinic.domain.owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.dto.OwnerResponseDto;
import org.springframework.samples.petclinic.domain.owner.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 주인 조회 서비스
 * - 주인 정보를 조회하고 응답 객체를 반환
 */
@Service
@RequiredArgsConstructor
public class OwnerReadService {

	private final OwnerRepository ownerRepository;

	/**
	 * 모든 회원 데이터를 조회
	 *
	 * @return OwnerResponseDto 각 회원의 이름, 주소, 전화번호, 도시 정보를 포함
	 */
	public List<OwnerResponseDto> findAll() {
		return findAllOwners();
	}

	/**
	 * ID로 특정 회원 데이터 조회
	 *
	 * @param id 조회할 회원의 고유 ID
	 * @return OwnerResponseDto 해당 회원의 이름, 주소, 전화번호, 도시 정보를 포함
	 */
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
			.orElseThrow(() -> new OwnerNotFoundException("Owner not found with id " + id));
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
