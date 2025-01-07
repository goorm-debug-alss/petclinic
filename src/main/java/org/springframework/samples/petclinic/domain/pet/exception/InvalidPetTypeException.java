package org.springframework.samples.petclinic.domain.pet.exception;

import org.springframework.samples.petclinic.common.error.PetErrorCode;

public class InvalidPetTypeException extends RuntimeException {
	private final PetErrorCode errorCode;

	public InvalidPetTypeException(PetErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}

	public PetErrorCode getErrorCode() {
		return errorCode;
	}
}
