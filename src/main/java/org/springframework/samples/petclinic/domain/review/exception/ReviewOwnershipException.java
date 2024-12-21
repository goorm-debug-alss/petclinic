package org.springframework.samples.petclinic.domain.review.exception;

public class ReviewOwnershipException extends RuntimeException {
	public ReviewOwnershipException(String message) {
		super(message);
	}
}
