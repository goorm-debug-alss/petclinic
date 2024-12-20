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
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;


/**
 * 후기 작성 요청을 처리하는 컨트롤러
 * - 이 클래스는 후기 생성 및 조회 요청을 처리하며, 서비스 계층과 연결되어 비즈니스 로직을 수행
 */
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewCreateService reviewCreateService;
	private final ReviewReadService reviewReadService;
	private final ReviewUpdateService reviewUpdateService;
	private final ReviewDeleteService reviewDeleteService;

	/**
	 * 리뷰 생성 API
	 *
	 * @param reviewRequestDto 리뷰 생성 요청 데이터
	 * @return 생성된 리뷰의 상세 정보를 반환
	 * */
	@PostMapping
	public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
		Integer ownerId = getAuthenticatedOwnerId();
		Integer vetId = reviewRequestDto.getVetId();
		ReviewResponseDto responseDto = reviewCreateService.createReview(reviewRequestDto, ownerId, vetId);
		return ResponseEntity.ok(responseDto);
	}

	/**
	 * 사용자 리뷰 조회 API
	 *
	 * @return 본인이 작성한 리뷰 목록을 반환
	 */
	@GetMapping("/my")
	public ResponseEntity<List<ReviewResponseDto>> getMyReviews() {
		Integer ownerId = getAuthenticatedOwnerId();
		Owner owner = Owner.builder().id(ownerId).build();
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByOwner(owner);
		return ResponseEntity.ok(reviews);
	}

	/**
	 * 특정 수의사 리뷰 조회 API
	 *
	 * @param vetId 조회할 수의사 ID
	 * @return 해당 수의사에 대한 리뷰 목록 반환
	 */
	@GetMapping("/{vetId}")
	public ResponseEntity<List<ReviewResponseDto>> getReviewsByVet(@PathVariable("vetId") Integer vetId) {
		Vet vet = Vet.builder().id(vetId).build();
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByVet(vet);
		return ResponseEntity.ok(reviews);
	}

	/**
	 * 리뷰 수정 API
	 *
	 * @param reviewId 수정할 리뷰의 ID
	 * @param reviewRequestDto 리뷰 수정 요청 데이터
	 * @return 수정된 리뷰의 상세 정보를 반환
	 */
	@PutMapping("/{reviewId}")
	public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("reviewId") Integer reviewId,
														  @RequestBody ReviewRequestDto reviewRequestDto) {
		Integer ownerId = getAuthenticatedOwnerId();
		Owner owner = Owner.builder().id(ownerId).build();
		ReviewResponseDto responseDto = reviewUpdateService.updateReview(reviewId, reviewRequestDto, owner);
		return ResponseEntity.ok(responseDto);
	}

	/**
	 * 리뷰 삭제 API
	 *
	 * @param reviewId 삭제할 리뷰의 ID
	 * @return 삭제 완료 상태를 나타내는 ResponseEntity(204)
	 */
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Integer reviewId) {
		Integer ownerId = getAuthenticatedOwnerId();
		Owner owner = Owner.builder().id(ownerId).build();
		reviewDeleteService.deleteReview(reviewId, owner);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 현재 인증된 사용자(Owner)의 ID
	 *
	 * @return 현재 요청의 ownerId
	 */
	private Integer getAuthenticatedOwnerId() {
		return (Integer) RequestContextHolder.currentRequestAttributes().getAttribute("ownerId", RequestAttributes.SCOPE_REQUEST);
	}
}
