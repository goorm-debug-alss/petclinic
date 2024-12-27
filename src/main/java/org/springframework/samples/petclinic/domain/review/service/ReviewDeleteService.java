package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.exception.ReviewOwnershipException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewDeleteService {

	private final ReviewRepository reviewRepository;
	private final ReviewEntityRetrievalService entityRetrievalService;


	// 특정 ID를 기반으로 후기 삭제
	public void deleteReview(Integer reviewId, Owner owner) {
		Review review = fetchReviewByIdOrThrow(reviewId);

		if (!review.getOwnerId().getId().equals(owner.getId()))
			throw new ReviewOwnershipException(owner.getId());

		reviewRepository.delete(review);
	}

	private Review fetchReviewByIdOrThrow(Integer reviewId) {
		return entityRetrievalService.fetchReviewByIdOrThrow(reviewId);
	}
}
