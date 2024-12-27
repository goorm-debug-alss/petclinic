package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.exception.ReviewNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.ReviewOwnershipException;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewUpdateService {

	private final ReviewEntityRetrievalService entityRetrievalService;

	private final ReviewRepository reviewRepository;


	// 리뷰를 업데이트하고 업데이트된 리뷰 반환
	public ReviewResponseDto updateReview(Integer reviewId, ReviewRequestDto dto, Owner owner) {
		Review review = fetchReviewByIdOrThrow(reviewId);
		validateOwnership(owner, review);

		Vet vet = fetchVetByIdOrThrow(dto);

		ReviewHelper.updateFields(dto, review, owner, vet);

		reviewRepository.save(review);

		return buildReviewResponse(review);
	}

	private Review fetchReviewByIdOrThrow(Integer reviewId) {
		return entityRetrievalService.fetchReviewByIdOrThrow(reviewId);
	}

	private static void validateOwnership(Owner owner, Review review) {
		if (review == null)
			throw new ReviewNotFoundException("리뷰가 존재하지 않습니다.");
		if (!owner.getId().equals(review.getOwnerId().getId())) {
			throw new ReviewOwnershipException(owner.getId());
		}
	}

	private Vet fetchVetByIdOrThrow(ReviewRequestDto dto) {
		return entityRetrievalService.fetchVetByIdOrThrow(dto.getVetId());
	}

	private static ReviewResponseDto buildReviewResponse(Review review) {
		return ReviewHelper.buildResponseDto(review);
	}
}
