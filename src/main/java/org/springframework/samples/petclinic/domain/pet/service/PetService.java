package org.springframework.samples.petclinic.domain.pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.enums.PetStatus;
import org.springframework.samples.petclinic.domain.pet.mapper.PetMapper;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.model.PetType;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.pet.repository.PetTypeRepository;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {

	private final PetRepository petRepository;
	private final PetTypeRepository petTypeRepository;
	private final OwnerRepository ownerRepository;
	private final PetMapper petMapper;

	// 모든 Pet 조회
	public List<PetResponseDto> getAllPets() {
		return petRepository.findAllByStatusOrderById(PetStatus.REGISTERED).stream()
			.map(petMapper::toDto)
			.collect(Collectors.toList());
	}

	// 단일 Pet 조회
	public PetResponseDto getPetById(Integer id) {
		Pet pet = petRepository.findByIdAndStatus(id,PetStatus.REGISTERED)
			.orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));
		return petMapper.toDto(pet);
	}

	// 주인의 펫 조회
	public List<PetResponseDto> getPetsByOwnerId(Integer ownerId) {
		ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ApiException(PetErrorCode.INVALID_OWNER));

		return petRepository.findAllByStatusOrderById(PetStatus.REGISTERED).stream()
			.filter(pet -> pet.getOwner() != null && pet.getOwner().getId().equals(ownerId))
			.map(petMapper::toDto)
			.collect(Collectors.toList());
	}

	// Pet 생성
	public PetResponseDto createPet(PetRequestDto request) {
		PetType petType = petTypeRepository.findById(request.getTypeId())
			.orElseThrow(() -> new ApiException(PetErrorCode.INVALID_PET_TYPE));

		Owner owner = ownerRepository.findById(request.getOwnerId())
			.orElseThrow(() -> new ApiException(PetErrorCode.INVALID_OWNER));

		Pet pet = petMapper.toEntity(request, petType, owner);
		Pet savedPet = petRepository.save(pet);

		return petMapper.toDto(savedPet);
	}

	// Pet 수정
	public PetResponseDto updatePet(Integer id, PetRequestDto request) {
		Pet pet = petRepository.findByIdAndStatus(id,PetStatus.REGISTERED)
			.orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));

		PetType petType = petTypeRepository.findById(request.getTypeId())
			.orElseThrow(() -> new ApiException(PetErrorCode.INVALID_PET_TYPE));

		Owner owner = ownerRepository.findById(request.getOwnerId())
			.orElseThrow(() -> new ApiException(PetErrorCode.INVALID_OWNER));

		// 업데이트 반영
		pet.setName(request.getName());
		pet.setBirthDate(request.getBirthDate());
		pet.setType(petType);
		pet.setOwner(owner);

		Pet updatedPet = petRepository.save(pet);
		return petMapper.toDto(updatedPet);
	}

	// Pet 삭제
	public void deletePet(Integer id) {
		Pet pet = petRepository.findByIdAndStatus(id,PetStatus.REGISTERED)
			.filter(Pet::isRegistered)
			.orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));
		pet.setStatus(PetStatus.DELETED);
	}
}
