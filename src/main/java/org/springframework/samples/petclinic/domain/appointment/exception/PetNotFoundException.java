package org.springframework.samples.petclinic.domain.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PetNotFoundException extends RuntimeException {
	public PetNotFoundException(Integer petId) {
		super("Appointment not found with ID: " + petId);
	}
}
