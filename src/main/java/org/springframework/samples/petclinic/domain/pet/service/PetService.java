package org.springframework.samples.petclinic.domain.pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {

	private final PetRepository petRepository;

	// 모든 Pet 조회
	public List<PetResponseDto> getAllPets() {
		return petRepository.findAll().stream()
			.map(pet -> PetResponseDto.builder()
				.id(pet.getId())
				.name(pet.getName())
				.birthDate(pet.getBirthDate())
				.typeId(pet.getTypeId())
				.ownerId(pet.getOwnerId())
				.build())
			.collect(Collectors.toList());
	}

	// 단일 Pet 조회
	public PetResponseDto getPetById(Long id) {
		Pet pet = petRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Pet not found"));
		return PetResponseDto.builder()
			.id(pet.getId())
			.name(pet.getName())
			.birthDate(pet.getBirthDate())
			.typeId(pet.getTypeId())
			.ownerId(pet.getOwnerId())
			.build();
	}

	// Pet 생성
	public PetResponseDto createPet(PetRequestDto request) {
		Pet pet = new Pet();
		pet.setName(request.getName());
		pet.setBirthDate(request.getBirthDate());
		pet.setTypeId(request.getTypeId());
		pet.setOwnerId(request.getOwnerId());
		Pet savedPet = petRepository.save(pet);

		return PetResponseDto.builder()
			.id(savedPet.getId())
			.name(savedPet.getName())
			.birthDate(savedPet.getBirthDate())
			.typeId(savedPet.getTypeId())
			.ownerId(savedPet.getOwnerId())
			.build();
	}

	// Pet 수정
	public PetResponseDto updatePet(Long id, PetRequestDto request) {
		Pet pet = petRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Pet not found"));
		pet.setName(request.getName());
		pet.setBirthDate(request.getBirthDate());
		pet.setTypeId(request.getTypeId());

		Pet updatedPet = petRepository.save(pet);
		return PetResponseDto.builder()
			.id(updatedPet.getId())
			.name(updatedPet.getName())
			.birthDate(updatedPet.getBirthDate())
			.typeId(updatedPet.getTypeId())
			.ownerId(updatedPet.getOwnerId())
			.build();
	}

	// Pet 삭제
	public void deletePet(Long id) {
		petRepository.deleteById(id);
	}
}
