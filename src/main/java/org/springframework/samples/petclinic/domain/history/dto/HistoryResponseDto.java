package org.springframework.samples.petclinic.domain.history.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoryResponseDto {

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
	public static class Body {
		private int historyId;
		private String symptoms;
		private String content;
		private int vetId;
		private int visitId;
	}
}
