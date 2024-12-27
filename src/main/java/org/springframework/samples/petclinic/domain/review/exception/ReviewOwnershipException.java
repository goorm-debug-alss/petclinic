package org.springframework.samples.petclinic.domain.review.exception;

public class ReviewOwnershipException extends RuntimeException {
	public ReviewOwnershipException(Integer ownerId) {
		super("You are not the owner of the review with ID: " + ownerId);
	}
}
