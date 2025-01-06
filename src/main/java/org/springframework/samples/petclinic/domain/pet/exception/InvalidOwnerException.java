package org.springframework.samples.petclinic.domain.pet.exception;

public class InvalidOwnerException extends RuntimeException {
	public InvalidOwnerException(String message) {
		super(message);
	}
}
