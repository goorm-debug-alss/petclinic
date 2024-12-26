package org.springframework.samples.petclinic.domain.pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.model.PetType;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.pet.repository.PetTypeRepository;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
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

	// 모든 Pet 조회
	public List<PetResponseDto> getAllPets() {
		return petRepository.findAll().stream()
			.map(pet -> PetResponseDto.builder()
				.id(pet.getId())
				.name(pet.getName())
				.birthDate(pet.getBirthDate())
				.typeId(pet.getTypeId() != null ? pet.getTypeId().getId() : null)
				.ownerId(pet.getOwnerId() != null ? pet.getOwnerId().getId() : null)
				.build())
			.collect(Collectors.toList());
	}

	// 단일 Pet 조회
	public PetResponseDto getPetById(Integer id) {
		Pet pet = petRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Pet not found"));

		return PetResponseDto.builder()
			.id(pet.getId())
			.name(pet.getName())
			.birthDate(pet.getBirthDate())
			.typeId(pet.getTypeId() != null ? pet.getTypeId().getId() : null)
			.ownerId(pet.getOwnerId() != null ? pet.getOwnerId().getId() : null)
			.build();
	}

	// 주인의 펫 조회
	public List<PetResponseDto> getPetsByOwnerId(Integer ownerId) {
		return petRepository.findAll().stream()
			.filter(pet -> pet.getOwnerId() != null && pet.getOwnerId().getId().equals(ownerId))
			.map(pet -> PetResponseDto.builder()
				.id(pet.getId())
				.name(pet.getName())
				.birthDate(pet.getBirthDate())
				.typeId(pet.getTypeId() != null ? pet.getTypeId().getId() : null)
				.ownerId(pet.getOwnerId().getId())
				.build())
			.collect(Collectors.toList());
	}

	// Pet 생성
	public PetResponseDto createPet(PetRequestDto request) {
		Pet pet = new Pet();
		pet.setName(request.getName());
		pet.setBirthDate(request.getBirthDate());

		PetType petType = petTypeRepository.findById(request.getTypeId())
			.orElseThrow(() -> new IllegalArgumentException("Invalid PetType ID"));
		pet.setTypeId(petType);

		Owner owner = ownerRepository.findById(request.getOwnerId())
			.orElseThrow(() -> new IllegalArgumentException("Invalid Owner ID"));
		pet.setOwnerId(owner);

		Pet savedPet = petRepository.save(pet);

		return PetResponseDto.builder()
			.id(savedPet.getId())
			.name(savedPet.getName())
			.birthDate(savedPet.getBirthDate())
			.typeId(savedPet.getTypeId().getId())
			.ownerId(savedPet.getOwnerId().getId())
			.build();
	}

	// Pet 수정
	public PetResponseDto updatePet(Integer id, PetRequestDto request) {
		Pet pet = petRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Pet not found"));

		pet.setName(request.getName());
		pet.setBirthDate(request.getBirthDate());

		PetType petType = petTypeRepository.findById(request.getTypeId())
			.orElseThrow(() -> new IllegalArgumentException("Invalid PetType ID"));
		pet.setTypeId(petType);

		Owner owner = ownerRepository.findById(request.getOwnerId())
			.orElseThrow(() -> new IllegalArgumentException("Invalid Owner ID"));
		pet.setOwnerId(owner);

		Pet updatedPet = petRepository.save(pet);

		return PetResponseDto.builder()
			.id(updatedPet.getId())
			.name(updatedPet.getName())
			.birthDate(updatedPet.getBirthDate())
			.typeId(updatedPet.getTypeId().getId())
			.ownerId(updatedPet.getOwnerId().getId())
			.build();
	}

	// Pet 삭제
	public void deletePet(Integer id) {
		petRepository.deleteById(id);
	}
}
