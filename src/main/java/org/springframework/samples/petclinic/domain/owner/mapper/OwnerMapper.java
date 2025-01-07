package org.springframework.samples.petclinic.domain.owner.mapper;

import org.springframework.samples.petclinic.domain.owner.dto.OwnerResponseDto;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class OwnerMapper {

	public OwnerResponseDto toDto(Owner owner) {
		return OwnerResponseDto.builder()
			.id(owner.getId())
			.name(owner.getName())
			.address(owner.getAddress())
			.city(owner.getCity())
			.telephone(owner.getTelephone())
			.build();
	}
}
