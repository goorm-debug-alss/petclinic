package org.springframework.samples.petclinic.domain.vet.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VetRequestDto {
	@NotBlank
	private String name;

	@NotNull
	@Size(min = 1)
	private List<Integer> specialties;
}
