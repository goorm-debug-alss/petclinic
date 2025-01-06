package org.springframework.samples.petclinic.domain.pet.exception;

public class InvalidPetTypeException extends RuntimeException {
	public InvalidPetTypeException(String message) {
		super(message);
	}
}
