package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.error.ReviewErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeleteReviewService {

	private final OwnerRepository ownerRepository;
	private final ReviewRepository reviewRepository;

	public void deleteReview(Integer reviewId, Integer ownerId) {
		Owner owner = getOwnerOrThrow(ownerId);

		Review review = getReviewOrThrow(reviewId);

		validateReviewOwnership(owner, review);

		reviewRepository.delete(review);
	}

	private Owner getOwnerOrThrow(Integer ownerId) {
		return ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ApiException(OwnerErrorCode.NO_OWNER));
	}

	private Review getReviewOrThrow(Integer reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));
	}

	private static void validateReviewOwnership(Owner owner, Review review) {
		if (!Objects.equals(owner.getId(), review.getOwner().getId()))
			throw new ApiException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
	}
}
