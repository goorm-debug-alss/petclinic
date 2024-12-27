package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.exception.InvalidContentException;
import org.springframework.samples.petclinic.domain.review.exception.InvalidScoreException;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReviewCreateService {

	private final ReviewRepository reviewRepository;
	private final VetRepository vetRepository;
	private final ReviewEntityRetrievalService entityRetrievalService;

	// 리뷰를 생성하고 저장된 리뷰 정보를 반환
	public ReviewResponseDto createReview(ReviewRequestDto dto, Integer ownerId, Integer vetId) {
		validateRequest(dto);

		Owner owner = fetchOwnerByIdOrThrow(ownerId);
		Vet vet = fetchVetByIdOrThrow(vetId);

		Review review = ReviewHelper.convertToReviewEntity(dto, owner, vet);
		Review savedReview = reviewRepository.save(review);

		updateVetRatingAndReviewCount(vet, dto.getScore());

		return buildReviewResponse(savedReview);
	}

	private void validateRequest(ReviewRequestDto requestDto) {
		validateScore(requestDto.getScore());
		validateContent(requestDto.getContent());
	}

	private static void validateScore(Integer score) {
		if (score == null || score < 1 || score> 5)
			throw new InvalidScoreException("평점은 1점 이상 5점 이하로 설정해야 합니다.");
	}

	private static void validateContent(String content) {
		if (content == null || content.isBlank())
			throw new InvalidContentException("리뷰 내용은 비어 있을 수 없습니다.");
	}

	private Owner fetchOwnerByIdOrThrow(Integer ownerId) {
		return entityRetrievalService.fetchOwnerByIdOrThrow(ownerId);
	}

	private Vet fetchVetByIdOrThrow(Integer vetId) {
		return entityRetrievalService.fetchVetByIdOrThrow(vetId);
	}

	private void updateVetRatingAndReviewCount(Vet vet, int newScore) {
		int currentReviewCount = vet.getReviewCount() != null ? vet.getReviewCount() : 0;
		BigDecimal updatedAverageRating = calculateUpdatedAverageRating(vet.getAverageRatings(), currentReviewCount, newScore);

		int updatedReviewCount = currentReviewCount + 1;
		vet.updateRatings(updatedAverageRating, updatedReviewCount);
		vetRepository.save(vet);
	}

	private BigDecimal calculateUpdatedAverageRating(BigDecimal currentAverageRating, int currentReviewCount, int newScore) {
		BigDecimal totalScore = (currentAverageRating != null ? currentAverageRating : BigDecimal.ZERO)
			.multiply(BigDecimal.valueOf(currentReviewCount))
			.add(BigDecimal.valueOf(newScore));
		return totalScore.divide(BigDecimal.valueOf(currentReviewCount + 1), 2, BigDecimal.ROUND_HALF_UP);
	}

	private static ReviewResponseDto buildReviewResponse(Review savedReview) {
		return ReviewHelper.buildResponseDto(savedReview);
	}
}
