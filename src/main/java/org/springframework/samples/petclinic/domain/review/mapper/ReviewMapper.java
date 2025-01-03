package org.springframework.samples.petclinic.domain.review.mapper;

import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

	public ReviewResponseDto toDto(Review review) {
		return ReviewResponseDto.builder()
			.id(review.getId())
			.score(review.getScore())
			.content(review.getContent())
			.createAt(review.getCreatedAt())
			.vetId(review.getVetId().getId())
			.ownerId(review.getOwnerId().getId())
			.build();
	}

	public Review toEntity(ReviewRequestDto request, Owner owner, Vet vet) {
		return Review.builder()
			.score(request.getScore())
			.content(request.getContent())
			.ownerId(owner)
			.vetId(vet)
			.build();
	}
}
