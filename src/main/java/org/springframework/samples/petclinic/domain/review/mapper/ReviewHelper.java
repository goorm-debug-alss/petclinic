package org.springframework.samples.petclinic.domain.review.mapper;

import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.Result;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.dto.StatusCode;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

/**
 * 리뷰 엔티티와 관련된 유틸리티 메서드를 제공
 */
public class ReviewHelper {

	/**
	 * 리뷰 엔티티의 필드 업데이트
	 *
	 * @param dto		리뷰 요청 데이터
	 * @param review   	리뷰 엔티티
	 * @param owner		리뷰 작성자
	 * @param vet		관련된 수의사
	 */
	public static void updateFields(ReviewRequestDto dto, Review review, Owner owner, Vet vet) {
		review.updateReview(
			dto.getScore(),
			dto.getContent(),
			owner,
			vet
		);
	}

	/**
	 * 리뷰 엔티티를 기반으로 응답 DTO 생성
	 *
	 * @param review 리뷰 엔티티
	 * @return 생성된 리뷰 응답 DTO
	 */
	public static ReviewResponseDto buildResponseDto(Review review) {
		//review가 null일 경유 예외를 명확히 처리하는 방어 코드
		if (review == null) {
			throw new IllegalArgumentException("Review cannot be null");
		}
		return ReviewResponseDto.builder()
			.result(Result.builder()
				.resultCode(StatusCode.SUCCESS.getCode())
				.resultDescription(StatusCode.SUCCESS.getDescription())
				.build())
			.body(buildBody(review))
			.build();
	}

	/**
	 * 리뷰 엔티티의 상세 데이터를 기반으로 Body 객체 생성
	 *
	 * @param review 리뷰 엔티티
	 * @return 생성된 Body 객체
	 */
	public static ReviewResponseDto.Body buildBody(Review review) {
		return ReviewResponseDto.Body.builder()
			.id(review.getId())
			.score(review.getScore())
			.content(review.getContent())
			.createdAt(review.getCreatedAt())
			.vetId(review.getVetId().getId())
			.ownerId(review.getOwnerId().getId())
			.build();
	}
}
