package org.springframework.samples.petclinic.domain.pet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.samples.petclinic.domain.pet.model.PetType;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PetRequestDto {
	private String name;
	private LocalDate birthDate;
	private Integer typeId;
	private Integer ownerId;
}
