package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.exception.ReviewOwnershipException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

/**
 * 후기 삭제 서비스
 * - 후기 ID를 기반으로 데이터를 삭제
 */
@Service
@RequiredArgsConstructor
public class ReviewDeleteService {

	private final ReviewRepository reviewRepository;
	private final ReviewEntityRetrievalService entityRetrievalService;

	/**
	 * 특정 ID를 기반으로 후기 삭제
	 *
	 * @param reviewId 삭제할 후기의 ID
	 * @param owner 후기 작성자 ID
	 */
	public void deleteReview(Integer reviewId, Owner owner) {
		Review review = entityRetrievalService.fetchReviewByIdOrThrow(reviewId);

		if (!review.getOwnerId().getId().equals(owner.getId()))
			throw new ReviewOwnershipException(owner.getId());

		reviewRepository.delete(review);
	}
}
