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

	/**
	 * 리뷰를 업데이트하고 업데이트된 리뷰 반환
	 *
	 * @param reviewId 	리뷰 ID
	 * @param dto		리뷰 요청 DTO
	 * @param owner		리뷰를 소유한 사용자 정보
	 * @return 업데이트된 리뷰에 대한 응답 DTO
	 */
	public ReviewResponseDto updateReview(Integer reviewId, ReviewRequestDto dto, Owner owner) {
		Review review = entityRetrievalService.fetchReviewByIdOrThrow(reviewId);
		validateOwnership(owner, review);

		Vet vet = entityRetrievalService.fetchVetByIdOrThrow(dto.getVetId());

		ReviewHelper.updateFields(dto, review, owner, vet);

		reviewRepository.save(review);

		return ReviewHelper.buildResponseDto(review);
	}

	private static void validateOwnership(Owner owner, Review review) {
		if (review == null)
			throw new ReviewNotFoundException("리뷰가 존재하지 않습니다.");
		if (!owner.getId().equals(review.getOwnerId().getId())) {
			throw new ReviewOwnershipException(owner.getId());
		}
	}
}
