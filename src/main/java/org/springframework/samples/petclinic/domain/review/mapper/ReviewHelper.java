package org.springframework.samples.petclinic.domain.review.mapper;

import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

public class ReviewHelper {

	public static void updateFields(ReviewRequestDto dto, Review review, Owner owner, Vet vet) {
		review.updateReview(
			dto.getScore(),
			dto.getContent(),
			owner,
			vet
		);
	}

	public static ReviewResponseDto buildResponseDto(Review review) {
		return ReviewResponseDto.builder()
			.id(review.getId())
			.score(review.getScore())
			.content(review.getContent())
			.createdAt(review.getCreatedAt())
			.vetId(review.getVetId().getId())
			.ownerId(review.getOwnerId().getId())
			.build();
	}

	public static Review convertToReviewEntity(ReviewRequestDto requestDto, Owner owner, Vet vet) {
		return Review.builder()
			.score(requestDto.getScore())
			.content(requestDto.getContent())
			.ownerId(owner)
			.vetId(vet)
			.build();
	}
}
