package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.error.ReviewErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewMapper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.service.VetService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateReviewService {

	private final ReviewRepository reviewRepository;
	private final OwnerRepository ownerRepository;
	private final VetRepository vetRepository;
	private final VetService vetService;
	private final ReviewMapper reviewMapper;

	public Review createReview(ReviewRequestDto request, Integer ownerId) {
		Vet vet = vetService.getVetOrThrow(request.getVetId());
		Owner owner = getOwnerOrThrow(ownerId);
		validateRequestData(request);

		Review review = reviewMapper.toEntity(request, owner, vet);
		Review savedReview = reviewRepository.save(review);
		updateVetRatingAndReviewCount(vet, request.getScore());

		return savedReview;
	}

	private Owner getOwnerOrThrow(Integer ownerId) {
		return ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ApiException(OwnerErrorCode.NO_OWNER));
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

	private void updateVetRatingAndReviewCount(Vet vet, int newScore) {
		int currentReviewCount = vet.getReviewCount() != null ? vet.getReviewCount() : 0;
		BigDecimal currentAverageRating = vet.getAverageRatings() != null ? vet.getAverageRatings() : BigDecimal.ZERO;

		BigDecimal updatedTotalScore = currentAverageRating.multiply(BigDecimal.valueOf(currentReviewCount))
			.add(BigDecimal.valueOf(newScore));
		int updatedReviewCount = currentReviewCount + 1;

		BigDecimal updatedAverageRating = updatedTotalScore.divide(BigDecimal.valueOf(updatedReviewCount), 2, BigDecimal.ROUND_HALF_UP);

		vet.updateRatings(updatedAverageRating, updatedReviewCount);
		vetRepository.save(vet);
	}
}
