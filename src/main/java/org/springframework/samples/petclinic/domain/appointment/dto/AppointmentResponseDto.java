package org.springframework.samples.petclinic.domain.appointment.dto;

import lombok.*;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
	private Integer id;

	private LocalDateTime apptDateTime;

	private ApptStatus status;
	private String symptoms;
	private Integer vetId;
	private Integer petId;

	public AppointmentResponseDto(Appointment appointment) {
		this.id = appointment.getId();
		this.apptDateTime = appointment.getApptDateTime();
		this.status = appointment.getStatus();
		this.symptoms = appointment.getSymptoms();
		this.petId = appointment.getPet().getId();
		this.vetId = appointment.getVet().getId();
	}
}
