package org.springframework.samples.petclinic.domain.vet.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.samples.petclinic.domain.speciality.model.Specialty;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VetResponseDto {
	private Integer id;

	private String name;

	private BigDecimal averageRatings;

	private Integer reviewCount;

	private List<Specialty> specialties;
}
