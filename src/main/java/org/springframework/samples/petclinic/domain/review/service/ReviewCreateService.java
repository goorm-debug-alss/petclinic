package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 리뷰 생성 서비스
 * - 리뷰 생성 로직 및 데이터베이스 연동 처리
 */
@Service
@RequiredArgsConstructor
public class ReviewCreateService {

	private final ReviewRepository reviewRepository;
	private final OwnerRepository ownerRepository;
	private final VetRepository vetRepository;

	/**
	 * 리뷰 생성
	 *
	 * @param requestDto 리뷰 요청 데이터
	 * @param ownerId 리뷰를 작성하는 소유자의 ID
	 * @param vetId 리뷰 대상인 수의사 ID
	 * @return 생성된 리뷰에 대한 응답 데이터
	 */
	@Transactional
	public ReviewResponseDto createReview(ReviewRequestDto requestDto, Integer ownerId, Integer vetId) {
		Owner owner = fetchOwnerByIdOrThrow(ownerId);
		Vet vet = fetchVetByIdOrThrow(vetId);

		Review review = convertToReviewEntity(requestDto, owner, vet);
		Review savedReview = reviewRepository.save(review);

		return createReviewResponse(savedReview);
	}

	private Owner fetchOwnerByIdOrThrow(Integer ownerId) {
		return ownerRepository.findById(ownerId).orElseThrow(() -> new OwnerNotFoundException("Invalid owner ID"));
	}

	private Vet fetchVetByIdOrThrow(Integer vetId) {
		return vetRepository.findById(vetId).orElseThrow(() -> new VetNotFoundException("Invalid vet ID"));
	}

	private static Review convertToReviewEntity(ReviewRequestDto requestDto, Owner owner, Vet vet) {
		return Review.builder()
			.score(requestDto.getScore())
			.content(requestDto.getContent())
			.ownerId(owner)
			.vetId(vet)
			.build();
	}

	private static ReviewResponseDto createReviewResponse(Review savedReview) {
		return ReviewHelper.buildResponseDto(savedReview);
	}
}
