package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.error.ReviewErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewMapper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UpdateReviewService {

	private final OwnerRepository ownerRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewMapper reviewMapper;
	private final VetRepository vetRepository;

	public ReviewResponseDto updateReview(ReviewRequestDto request, Integer ownerId, Integer reviewId) {
		validateRequestData(request);

		Review review = getReviewOrThrow(reviewId);
		validateOwner(ownerId, review);

		Vet vet = getVetOrThrow(review);
		updateVetRatingsIfNeeded(request, review, vet);

		review.updateReview(request.getScore(), request.getContent());
		Review updateReview = reviewRepository.save(review);

		return reviewMapper.toDto(updateReview);
	}

	private void validateRequestData(ReviewRequestDto request) {
		validateReviewScore(request.getScore());
		validateReviewContent(request.getContent());
	}

	private void validateReviewScore(Integer score) {
		if (score == null || score < 1 || score > 5)
			throw new ApiException(ReviewErrorCode.INVALID_REVIEW_SCORE);
	}

	private void validateReviewContent(String content) {
		if (content == null || content.trim().isEmpty())
			throw new ApiException(ReviewErrorCode.INVALID_REVIEW_CONTENT);

		if (content.length() < 10)
			throw new ApiException(ReviewErrorCode.REVIEW_CONTENT_TOO_SHORT);

		if (content.length() > 200)
			throw new ApiException(ReviewErrorCode.REVIEW_CONTENT_TOO_LONG);
	}

	private Review getReviewOrThrow(Integer reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));
	}

	private void validateOwner(Integer ownerId, Review review) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ApiException(OwnerErrorCode.NO_OWNER));

		if (!review.getOwnerId().getId().equals(owner.getId()))
			throw new ApiException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
	}

	private Vet getVetOrThrow(Review review) {
		return vetRepository.findById(review.getVetId().getId())
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));
	}

	private void updateVetRatingsIfNeeded(ReviewRequestDto request, Review review, Vet vet) {
		if (!review.getScore().equals(request.getScore()))
			updateVetRatingAndReviewCount(vet, review.getScore(), request.getScore());
	}

	private void updateVetRatingAndReviewCount(Vet vet, int oldScore, int newScore) {
		int currentReviewCount = vet.getReviewCount() != null ? vet.getReviewCount() : 0;
		BigDecimal currentAverageRating = vet.getAverageRatings() != null ? vet.getAverageRatings() : BigDecimal.ZERO;

		BigDecimal updatedTotalScore = currentAverageRating.multiply(BigDecimal.valueOf(currentReviewCount))
			.subtract(BigDecimal.valueOf(oldScore))
			.add(BigDecimal.valueOf(newScore));

		BigDecimal updatedAverageRating = currentReviewCount > 0
			? updatedTotalScore.divide(BigDecimal.valueOf(currentReviewCount), 2, BigDecimal.ROUND_HALF_UP)
			: BigDecimal.ZERO;

		vet.updateRatings(updatedAverageRating, currentReviewCount);
		vetRepository.save(vet);
	}
}

