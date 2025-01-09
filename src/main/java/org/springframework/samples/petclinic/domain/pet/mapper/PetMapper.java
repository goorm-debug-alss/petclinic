package org.springframework.samples.petclinic.domain.pet.mapper;

import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.enums.PetStatus;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.model.PetType;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

	public PetResponseDto toDto(Pet pet) {
		return PetResponseDto.builder()
			.id(pet.getId())
			.name(pet.getName())
			.birthDate(pet.getBirthDate())
			.typeId(pet.getType().getId())
			.ownerId(pet.getOwner().getId())
			.build();
	}

	public Pet toEntity(PetRequestDto petRequestDto, PetType type, Owner owner) {
		return Pet.builder()
			.name(petRequestDto.getName())
			.birthDate(petRequestDto.getBirthDate())
			.type(type)
			.owner(owner)
			.status(PetStatus.REGISTERED)
			.build();
	}
}
