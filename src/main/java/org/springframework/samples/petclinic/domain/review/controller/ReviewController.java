package org.springframework.samples.petclinic.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.review.dto.ReviewDto;
import org.springframework.samples.petclinic.domain.review.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
	private final ReviewService reviewService;

	// 리뷰 생성
	@PostMapping
	public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDTO, Principal principal) {
		reviewDTO.setOwnerId(Integer.parseInt(principal.getName()));
		return ResponseEntity.ok(reviewService.saveReview(reviewDTO));
	}

	// 본인의 리뷰 조회
	@GetMapping("/my")
	public ResponseEntity<List<ReviewDto>> getMyAllReviews(Principal principal) {
		Integer ownerId = Integer.parseInt(principal.getName());
		return ResponseEntity.ok(reviewService.getReviewsByOwner(ownerId));
	}

	// 리뷰 수정
	@PutMapping("/{id}")
	public ResponseEntity<ReviewDto> updateReview(@PathVariable Integer id,
												  @RequestBody ReviewDto reviewDTO,
												  Principal principal) {
		Integer ownerId = Integer.parseInt(principal.getName());
		return ResponseEntity.ok(reviewService.updateReview(id, reviewDTO, ownerId));
	}

	// 리뷰 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Integer id, Principal principal) {
		Integer ownerId = Integer.parseInt(principal.getName());
		reviewService.deleteReview(id, ownerId);
		return ResponseEntity.noContent().build();
	}
}

