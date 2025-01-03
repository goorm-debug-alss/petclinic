package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ReviewErrorCode implements ErrorCodeInterface {

	NO_REVIEW(HttpStatus.BAD_REQUEST.value(), 701, "해당 리뷰가 존재하지 않습니다."),
	INVALID_REVIEW_SCORE(HttpStatus.BAD_REQUEST.value(), 702, "리뷰 점수가 유효하지 않습니다."),
	INVALID_REVIEW_CONTENT(HttpStatus.BAD_REQUEST.value(), 703, "유효하지 않은 리뷰 내용입니다."),
	REVIEW_CONTENT_TOO_SHORT(HttpStatus.BAD_REQUEST.value(), 704, "리뷰 내용이 너무 짧습니다."),
	REVIEW_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST.value(), 705, "리뷰 내용이 너무 깁니다."),
	DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST.value(), 706, "이미 리뷰를 작성한 사용자입니다."),
	UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.BAD_REQUEST.value(), 707, "본인의 리뷰만 접근 가능합니다.");

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;
}
