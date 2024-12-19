package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.review.dto.ReviewDto;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	//리뷰 저장

	@Transactional
	public ReviewDto saveReview(ReviewDto dto) {
		// 평점 검증
		if (dto.getScore() < 1 || dto.getScore() > 5) {
			throw new IllegalArgumentException("평점은 1점 이상 5점 이하로 설정해야 합니다.");
		}

		// 연관 엔티티 생성
		Owner owner = Owner.builder().id(dto.getOwnerId()).build();
		Vet vet = Vet.builder().id(dto.getVetId()).build();

		// Review 엔터티 생성 및 저장
		Review review = Review.builder()
			.score(dto.getScore())
			.content(dto.getContent())
			.createdAt(LocalDateTime.now())
			.ownerId(owner)
			.vetId(vet)
			.build();

		Review savedReview = reviewRepository.save(review);
		return toDto(savedReview);
	}

	//본인의 리뷰 조회

	public List<ReviewDto> getReviewsByOwner(Integer ownerId) {
		return reviewRepository.findByOwnerId_Id(ownerId).stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	//수의사별 리뷰 조회

	public List<ReviewDto> getReviewsByVet(Integer vetId) {
		return reviewRepository.findByVetId_Id(vetId).stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}


	 //특정 리뷰 조회
	public ReviewDto getReviewById(Integer reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
		return toDto(review);
	}

	//전체 리뷰 조회
	public List<ReviewDto> getAllReviews() {
		return reviewRepository.findAll().stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	//리뷰 수정
	@Transactional
	public ReviewDto updateReview(Integer reviewId, ReviewDto dto, Integer ownerId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

		if (!review.getOwnerId().getId().equals(ownerId)) {
			throw new SecurityException("본인의 리뷰만 수정할 수 있습니다.");
		}

		// 평점 검증
		if (dto.getScore() < 1 || dto.getScore() > 5) {
			throw new IllegalArgumentException("평점은 1점 이상 5점 이하로 설정해야 합니다.");
		}

		review.setScore(dto.getScore());
		review.setContent(dto.getContent());
		return toDto(reviewRepository.save(review));
	}

	//리뷰 삭제
	@Transactional
	public void deleteReview(Integer reviewId, Integer ownerId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

		if (!review.getOwnerId().getId().equals(ownerId)) {
			throw new SecurityException("본인의 리뷰만 삭제할 수 있습니다.");
		}

		reviewRepository.delete(review);
	}

	//Review 엔터티를 ReviewDto로 변환

	private ReviewDto toDto(Review review) {
		return new ReviewDto(
			review.getId(),
			review.getScore(),
			review.getContent(),
			review.getCreatedAt(),
			review.getVetId() != null ? review.getVetId().getId() : null,
			review.getOwnerId().getId()
		);
	}
}
