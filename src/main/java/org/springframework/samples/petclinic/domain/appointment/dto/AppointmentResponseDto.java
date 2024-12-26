package org.springframework.samples.petclinic.domain.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponseDto {

	private Integer id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime apptDateTime;

	private ApptStatus status;
	private String symptoms;
	private String petName;
	private String vetName;

}
