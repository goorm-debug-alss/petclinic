package org.springframework.samples.petclinic.domain.appointment.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentResponseDto {
	private Result result;
	private Body body;

	@Data
	@Builder
	public static class Body {
		private Integer id;
		private LocalDate apptDate;
		private ApptStatus status;
		private String symptoms;
		private String petName;
		private String vetName;
	}
}
