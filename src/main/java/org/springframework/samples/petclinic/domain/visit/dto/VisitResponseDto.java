package org.springframework.samples.petclinic.domain.visit.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VisitResponseDto {

	private Result result;
	private Body body;

	@Data
	@Builder
	public static class Result {

		private String resultCode;
		private String resultDescription;
	}

	@Data
	@Builder
	public static class Body{

		private int petId;
		private LocalDateTime visitDate;
		private String description;
		private String petName;
	}
}
