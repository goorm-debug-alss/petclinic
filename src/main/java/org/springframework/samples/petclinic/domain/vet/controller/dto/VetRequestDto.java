package org.springframework.samples.petclinic.domain.vet.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.samples.petclinic.speciality.dto.SpecialityRequestDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VetRequestDto {
	@NotBlank
	private String name;

	@NotBlank
	private List<SpecialityRequestDto> specialties;
}
