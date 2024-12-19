package org.springframework.samples.petclinic.domain.appointment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
	private String resultCode;
	private String resultDescription;
}
