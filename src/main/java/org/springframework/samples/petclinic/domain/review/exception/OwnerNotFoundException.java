package org.springframework.samples.petclinic.domain.review.exception;

public class OwnerNotFoundException extends RuntimeException {
	public OwnerNotFoundException(Integer ownerId) {
		super("Owner not found with ID: " + ownerId);
	}
}
