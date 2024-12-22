package org.springframework.samples.petclinic.domain.review.exception;

public class InvalidContentException extends RuntimeException {
	public InvalidContentException(String message) {
		super(message);
	}
}
