package org.springframework.samples.petclinic.domain.history.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
