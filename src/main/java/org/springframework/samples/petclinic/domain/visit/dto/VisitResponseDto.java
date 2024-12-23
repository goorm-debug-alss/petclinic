package org.springframework.samples.petclinic.domain.visit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime visitDate;
		private String description;
	}
}
