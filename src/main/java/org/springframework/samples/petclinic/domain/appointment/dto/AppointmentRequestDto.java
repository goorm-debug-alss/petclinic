package org.springframework.samples.petclinic.domain.appointment.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentRequestDto {
	private LocalDate apptDate;
	private ApptStatus status;
	private String symptoms;
	private Integer petId;
	private Integer vetId;
}
