package org.springframework.samples.petclinic.domain.appointment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResultResponseDto<T> {
	private Result result;
	private List<T> body;
}
