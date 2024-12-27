package org.springframework.samples.petclinic.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.service.ReviewCreateService;
import org.springframework.samples.petclinic.domain.review.service.ReviewDeleteService;
import org.springframework.samples.petclinic.domain.review.service.ReviewReadService;
import org.springframework.samples.petclinic.domain.review.service.ReviewUpdateService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewCreateService reviewCreateService;
	private final ReviewReadService reviewReadService;
	private final ReviewUpdateService reviewUpdateService;
	private final ReviewDeleteService reviewDeleteService;

	// 리뷰 생성
	@PostMapping
	public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
		Integer ownerId = getAuthenticatedOwnerId();
		Integer vetId = reviewRequestDto.getVetId();
		ReviewResponseDto responseDto = reviewCreateService.createReview(reviewRequestDto, ownerId, vetId);
		return ResponseEntity.ok(responseDto);
	}

	// 사용자 리뷰 조회
	@GetMapping("/my")
	public ResponseEntity<List<ReviewResponseDto>> getMyReviews() {
		Integer ownerId = getAuthenticatedOwnerId();
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByOwner(ownerId);
		return ResponseEntity.ok(reviews);
	}

	// 수의사 리뷰 조회
	@GetMapping("/{vetId}")
	public ResponseEntity<List<ReviewResponseDto>> getReviewsByVet(@PathVariable("vetId") Integer vetId) {
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByVet(vetId);
		return ResponseEntity.ok(reviews);
	}

	// 리뷰 수정
	@PutMapping("/{reviewId}")
	public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("reviewId") Integer reviewId,
														  @RequestBody ReviewRequestDto reviewRequestDto) {
		Integer ownerId = getAuthenticatedOwnerId();
		Owner owner = Owner.builder().id(ownerId).build();
		ReviewResponseDto responseDto = reviewUpdateService.updateReview(reviewId, reviewRequestDto, owner);
		return ResponseEntity.ok(responseDto);
	}

	// 리뷰 삭제
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Integer reviewId) {
		Integer ownerId = getAuthenticatedOwnerId();
		Owner owner = Owner.builder().id(ownerId).build();
		reviewDeleteService.deleteReview(reviewId, owner);
		return ResponseEntity.noContent().build();
	}

	private Integer getAuthenticatedOwnerId() {
		return (Integer) RequestContextHolder.currentRequestAttributes().getAttribute("ownerId", RequestAttributes.SCOPE_REQUEST);
	}
}
