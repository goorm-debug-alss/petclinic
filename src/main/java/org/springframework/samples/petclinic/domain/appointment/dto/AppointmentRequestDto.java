package org.springframework.samples.petclinic.domain.appointment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDto {

	@NotNull
	private Integer vetId;

	@NotNull
	private Integer petId;

	@NotNull
	private LocalDateTime apptDateTime;

	private ApptStatus appStatus;

	@NotNull
	private String symptoms;
}
