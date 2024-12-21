package org.springframework.samples.petclinic.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ReviewResponseDto {
	private Result result;
	private Body body;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Body {
		private Integer id;
		private Integer score;
		private String content;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createdAt;

		private Integer vetId;
		private Integer ownerId;
	}
}
