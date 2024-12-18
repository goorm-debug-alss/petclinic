package org.springframework.samples.petclinic.domain.visit.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class VisitRequestDto {

	private int petId;
	private LocalDateTime visitDate;
	private String description;
}
