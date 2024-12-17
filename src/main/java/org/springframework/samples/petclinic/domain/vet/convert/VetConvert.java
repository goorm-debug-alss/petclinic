package org.springframework.samples.petclinic.domain.vet.convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.speciality.service.SpecialityService;
import org.springframework.stereotype.Component;

@Component
public class VetConvert {

	@Autowired
	private SpecialityService specialityService;

	public VetResponseDto toResponse(Vet vet) {
		return VetResponseDto.builder()
			.id(vet.getId())
			.name(vet.getName())
			.averageRatings(vet.getAverageRatings())
			.reviewCount(vet.getReviewCount())
			.specialties(specialityService.find(vet.getId()))
			.build();
	}
}
