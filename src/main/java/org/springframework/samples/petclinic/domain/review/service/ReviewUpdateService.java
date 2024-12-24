package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.exception.ReviewNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewUpdateService {

	private final ReviewRepository reviewRepository;
	private final VetRepository vetRepository;

	/**
	 * 리뷰를 업데이트하고 업데이트된 리뷰 반환
	 *
	 * @param reviewId 	리뷰 ID
	 * @param dto		리뷰 요청 DTO
	 * @param owner		리뷰를 소유한 사용자 정보
	 * @return 업데이트된 리뷰에 대한 응답 DTO
	 */
	@Transactional
	public ReviewResponseDto updateReview(Integer reviewId, ReviewRequestDto dto, Owner owner) {
		Review review = fetchByReviewIdOrThrow(reviewId);

		validateReviewOwnership(owner, review);

		//리뷰 내용이 비어있는지 먼저 확인
		if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
			throw new IllegalArgumentException("Review content cannot be empty");
		}

		Vet vet = fetchByVetIdOrThrow(dto);

		updateReviewDetails(dto, owner, review, vet);

		Review updatedReview = reviewRepository.save(review);
		return buildReviewResponse(updatedReview);
	}

	private Review fetchByReviewIdOrThrow(Integer reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException("Review not found"));
	}

	private static void validateReviewOwnership(Owner owner, Review review) {
		if (!review.getOwnerId().getId().equals(owner.getId())) {
			throw new SecurityException("본인의 리뷰만 수정 가능");
		}
	}

	private static void updateReviewDetails(ReviewRequestDto dto, Owner owner, Review review, Vet vet) {

		if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
			throw new IllegalArgumentException("Review content cannot be empty");
		}

		ReviewHelper.updateFields(dto, review, owner, vet);
	}

	private Vet fetchByVetIdOrThrow(ReviewRequestDto dto) {
		return vetRepository.findById(dto.getVetId())
			.orElseThrow(() -> new VetNotFoundException("Vet not found"));
	}

	private static ReviewResponseDto buildReviewResponse(Review updatedReview) {
		return ReviewHelper.buildResponseDto(updatedReview);
	}
}
