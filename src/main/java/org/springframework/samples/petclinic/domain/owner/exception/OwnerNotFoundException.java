package org.springframework.samples.petclinic.domain.owner.exception;

public class OwnerNotFoundException extends RuntimeException {
	public OwnerNotFoundException(String message) {
		super(message);
	}
}
