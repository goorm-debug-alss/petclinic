package org.springframework.samples.petclinic.domain.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentRequestDto {
	private Integer petId;
	private Integer vetId;
	private LocalDate apptDate;
	private ApptStatus status;
	private String symptoms;
}
