package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.error.ReviewErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeleteReviewService {

	private final OwnerRepository ownerRepository;
	private final ReviewRepository reviewRepository;
	private final VetRepository vetRepository;


	public void deleteReview(Integer reviewId, Integer ownerId) {
		Owner owner = getOwnerOrThrow(ownerId);
		Review review = getReviewOrThrow(reviewId);

		validateReviewOwnership(owner, review);

		Vet vet = getVetOrThrow(review.getVet().getId());

		reviewRepository.delete(review);

		updateVetRatingAndReviewCount(vet, review.getScore());
	}

	private Owner getOwnerOrThrow(Integer ownerId) {
		return ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ApiException(OwnerErrorCode.NO_OWNER));
	}

	private Review getReviewOrThrow(Integer reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));
	}
	private Vet getVetOrThrow(Integer vetId) {
		return vetRepository.findById(vetId)
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));
	}
	private static void validateReviewOwnership(Owner owner, Review review) {
		if (!Objects.equals(owner.getId(), review.getOwner().getId()))
			throw new ApiException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
	}

	private void updateVetRatingAndReviewCount(Vet vet, int removedScore) {
		int currentReviewCount = vet.getReviewCount() != null ? vet.getReviewCount() : 0;
		BigDecimal currentAverageRating = vet.getAverageRatings() != null ? vet.getAverageRatings() : BigDecimal.ZERO;

		// 리뷰 삭제에 따른 총 점수 업데이트
		BigDecimal updatedTotalScore = currentAverageRating.multiply(BigDecimal.valueOf(currentReviewCount))
			.subtract(BigDecimal.valueOf(removedScore));

		// 리뷰 수 감소
		int updatedReviewCount = Math.max(currentReviewCount - 1, 0);

		// 평균 평점 계산
		BigDecimal updatedAverageRating = updatedReviewCount > 0
			? updatedTotalScore.divide(BigDecimal.valueOf(updatedReviewCount), 2, BigDecimal.ROUND_HALF_UP)
			: BigDecimal.ZERO;

		// 수의사 객체 업데이트
		vet.updateRatings(updatedAverageRating, updatedReviewCount);
		vetRepository.save(vet);
	}
}

