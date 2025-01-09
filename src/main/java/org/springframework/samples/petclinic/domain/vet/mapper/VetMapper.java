package org.springframework.samples.petclinic.domain.vet.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.domain.vet.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.enums.VetStatus;
import org.springframework.samples.petclinic.domain.vet.service.SpecialtyService;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

	@Autowired
	private SpecialtyService specialtyService;

	public Vet toEntity(VetRequestDto vetRequestDto) {
		return Vet.builder()
			.name(vetRequestDto.getName())
			.status(VetStatus.REGISTERED)
			.build();
	}

	public VetResponseDto toResponse(Vet vet) {
		return VetResponseDto.builder()
			.id(vet.getId())
			.name(vet.getName())
			.averageRatings(vet.getAverageRatings())
			.reviewCount(vet.getReviewCount())
			.specialties(specialtyService.find(vet.getId()))
			.build();
	}
}
