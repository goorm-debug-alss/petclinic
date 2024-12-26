package org.springframework.samples.petclinic.domain.pet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.samples.petclinic.domain.pet.model.PetType;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class PetRequestDto {
	private String name;
	private LocalDate birthDate;
	private Integer typeId;
	private Integer ownerId;
}
