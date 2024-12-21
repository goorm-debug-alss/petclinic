package org.springframework.samples.petclinic.domain.review.exception;

public class InvalidScoreException extends RuntimeException {
	public InvalidScoreException(String message) {
		super(message);
	}
}
