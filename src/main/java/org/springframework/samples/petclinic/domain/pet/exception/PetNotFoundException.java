package org.springframework.samples.petclinic.domain.pet.exception;

import org.springframework.samples.petclinic.common.error.PetErrorCode;

public class PetNotFoundException extends RuntimeException {
	private final PetErrorCode errorCode;

	public PetNotFoundException(PetErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}

	public PetErrorCode getErrorCode() {
		return errorCode;
	}
}
