package org.springframework.samples.petclinic.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.samples.petclinic.domain.review.model.Review;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {

	private Integer id;
	private Integer score;
	private String content;
	private LocalDateTime createAt;
	private Integer vetId;
	private Integer ownerId;

	public ReviewResponseDto(Review review) {
		this.id = review.getId();
		this.score = review.getScore();
		this.content = review.getContent();
		this.createAt = LocalDateTime.now();
		this.vetId = review.getVet().getId();
		this.ownerId = review.getOwner().getId();
	}
}
