package org.springframework.samples.petclinic.domain.visit.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VisitResponseDto {

	private Result result;
	private List<Body> body;

	@Data
	@Builder
	public static class Result {

		private String resultCode;
		private String resultDescription;
	}

	@Data
	@Builder
	public static class Body{

		private int visitId;
		private String petName;
		private LocalDateTime visitDate;
		private String description;
	}
}
