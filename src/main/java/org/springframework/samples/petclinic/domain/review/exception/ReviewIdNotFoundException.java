package org.springframework.samples.petclinic.domain.review.exception;

public class ReviewIdNotFoundException extends RuntimeException {
	public ReviewIdNotFoundException(Integer reviewId) {
		super("Review not found with ID: " + reviewId);
	}
}
