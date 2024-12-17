package org.springframework.samples.petclinic.domain.speciality.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialityRequestDto {
	private String name;
}
