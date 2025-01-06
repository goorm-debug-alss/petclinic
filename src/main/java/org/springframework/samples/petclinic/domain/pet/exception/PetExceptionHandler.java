package org.springframework.samples.petclinic.domain.pet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 전역 예외 처리기
@RestControllerAdvice
public class PetExceptionHandler {

	@ExceptionHandler(PetNotFoundException.class)
	public ResponseEntity<String> handlePetNotFound(PetNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}

	@ExceptionHandler(InvalidPetTypeException.class)
	public ResponseEntity<String> handleInvalidPetType(InvalidPetTypeException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}

	@ExceptionHandler(InvalidOwnerException.class)
	public ResponseEntity<String> handleInvalidOwner(InvalidOwnerException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}
}
