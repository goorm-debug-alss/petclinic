package org.springframework.samples.petclinic.domain.pet.exception;

public class PetNotFoundException extends RuntimeException {
	public PetNotFoundException(String message) {
		super(message);
	}
}
