package org.springframework.samples.petclinic.domain.pet.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.samples.petclinic.domain.pet.model.PetType;

import java.time.LocalDate;

@Getter
@Builder
public class PetResponseDto {
	private Integer id;
	private String name;
	private LocalDate birthDate;
	private Integer typeId;
	private Integer ownerId;
}
