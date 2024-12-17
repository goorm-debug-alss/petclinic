package org.springframework.samples.petclinic.domain.pet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PetRequestDto {
	private String name;
	private String birthDate;
	private Integer typeId;
	private Integer ownerId;
}
