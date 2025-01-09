package org.springframework.samples.petclinic.domain.vet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VetRequestDto {
	private String name;

	private List<Integer> specialties;
}
