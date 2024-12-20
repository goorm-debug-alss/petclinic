package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 조회 서비스
 * - 리뷰와 관련된 읽기 작업을 처리
 */
@Service
@RequiredArgsConstructor
public class ReviewReadService {

	private final ReviewRepository reviewRepository;

	/**
	 * 특정 소유자가 작성한 리뷰 목록 조회
	 *
	 * @param owner 리뷰를 작성한 소유자
	 * @return 소유자가 작성한 리뷰의 리스트
	 */
	public List<ReviewResponseDto> getReviewsByOwner(Owner owner) {
		return fetchReviews(reviewRepository.findByOwnerId(owner.getId()));
	}

	/**
	 * 특정 수의사에 대한 리뷰 목록을 조회
	 *
	 * @param vet 리뷰 대상 수의사
	 * @return 수의사에 대한 리뷰의 리스트
	 */
	public List<ReviewResponseDto> getReviewsByVet(Vet vet) {
		return fetchReviews(reviewRepository.findByVetId(vet.getId()));
	}

	private List<ReviewResponseDto> fetchReviews(List<Review> reviewRepository) {
		return reviewRepository.stream()
			.map(ReviewHelper::buildResponseDto)
			.collect(Collectors.toList());
	}
}
