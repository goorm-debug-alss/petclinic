package org.springframework.samples.petclinic.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.service.CreateReviewService;
import org.springframework.samples.petclinic.domain.review.service.DeleteReviewService;
import org.springframework.samples.petclinic.domain.review.service.ReadReviewService;
import org.springframework.samples.petclinic.domain.review.service.UpdateReviewService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

	private final CreateReviewService createReviewService;
	private final ReadReviewService readReviewService;
	private final UpdateReviewService updateReviewService;
	private final DeleteReviewService deleteReviewService;

	// 리뷰 생성
	@PostMapping
	public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto request) {
		Integer ownerId = getAuthenticatedOwnerId();
		Review review = createReviewService.createReview(request, ownerId);
		ReviewResponseDto response = new ReviewResponseDto(review);
		return ResponseEntity.ok(response);
	}

	// 사용자 리뷰 조회
	@GetMapping("/my")
	public ResponseEntity<List<ReviewResponseDto>> getMyReviews() {
		Integer ownerId = getAuthenticatedOwnerId();
		return ResponseEntity.ok(readReviewService.findMyReviews(ownerId));
	}

	// 수의사 리뷰 조회
	@GetMapping("/{vetId}")
	public ResponseEntity<List<ReviewResponseDto>> getVetReviews(@PathVariable("vetId") Integer vetId) {
		return ResponseEntity.ok(readReviewService.findVetReviews(vetId));
	}

	// 리뷰 수정
	@PutMapping("/{reviewId}")
	public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("reviewId") Integer reviewId,
														  @RequestBody ReviewRequestDto request) {
		Integer ownerId = getAuthenticatedOwnerId();
		ReviewResponseDto response = updateReviewService.updateReview(request, ownerId, reviewId);
		return ResponseEntity.ok(response);
	}

	// 리뷰 삭제
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ReviewResponseDto> deleteReview(@PathVariable("reviewId") Integer reviewId) {
		Integer ownerId = getAuthenticatedOwnerId();
		deleteReviewService.deleteReview(reviewId, ownerId);
		return ResponseEntity.ok().build();
	}

	private Integer getAuthenticatedOwnerId() {
		return (Integer) RequestContextHolder.currentRequestAttributes().getAttribute("ownerId", RequestAttributes.SCOPE_REQUEST);
	}
}
