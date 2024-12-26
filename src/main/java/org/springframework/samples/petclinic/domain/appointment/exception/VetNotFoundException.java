package org.springframework.samples.petclinic.domain.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Vet 리소스를 찾을 수 없을 때 발생하는 예외입니다.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class VetNotFoundException extends RuntimeException {
	public VetNotFoundException(Integer vetId) {
		super("Vet not found with ID: " + vetId);
	}
}
