package org.springframework.samples.petclinic.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
	private Integer id; // Review 식별자
	private Integer score; // 평점
	private String content; // 리뷰 내용
	private LocalDateTime createdAt; // 생성일
	private Integer vetId; // 수의사 ID
	private Integer ownerId; // 주인 ID
}
