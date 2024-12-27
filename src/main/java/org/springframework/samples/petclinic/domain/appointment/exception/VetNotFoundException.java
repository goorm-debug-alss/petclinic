package org.springframework.samples.petclinic.domain.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VetNotFoundException extends RuntimeException {
	public VetNotFoundException(Integer vetId) {
		super("Vet not found with ID: " + vetId);
	}
}
