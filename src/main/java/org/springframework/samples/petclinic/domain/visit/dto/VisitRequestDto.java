package org.springframework.samples.petclinic.domain.visit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequestDto {

	private int petId;
	private LocalDateTime visitDate;
	private String description;
}
