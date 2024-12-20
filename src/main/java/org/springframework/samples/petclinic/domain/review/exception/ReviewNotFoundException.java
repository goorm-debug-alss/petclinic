package org.springframework.samples.petclinic.domain.review.exception;

public class ReviewNotFoundException extends RuntimeException {
	public ReviewNotFoundException(String message) {
		super(message);
	}
}
