package org.springframework.samples.petclinic.domain.pet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetResponseDto {
	private Long id;
	private String name;
	private String birthDate;
	private Integer typeId;
	private Integer ownerId;
}
