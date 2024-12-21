package org.springframework.samples.petclinic.domain.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VetNotFoundException extends RuntimeException {
	public VetNotFoundException(String message) {
		super(message);
	}
}
