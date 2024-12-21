package org.springframework.samples.petclinic.domain.review.exception;

public class OwnerNotFoundException extends RuntimeException {
	public OwnerNotFoundException(String message) {
		super(message);
	}
}
