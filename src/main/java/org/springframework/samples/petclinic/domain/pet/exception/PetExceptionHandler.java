package org.springframework.samples.petclinic.domain.pet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.samples.petclinic.common.error.PetErrorCode;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class PetExceptionHandler {

	@ExceptionHandler(InvalidPetTypeException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidPetType(InvalidPetTypeException e) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", e.getErrorCode().getHttpStatusCode());
		errorResponse.put("error", e.getErrorCode().getErrorCode());
		errorResponse.put("message", e.getErrorCode().getDescription());
		return ResponseEntity.status(e.getErrorCode().getHttpStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(InvalidOwnerException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidOwner(InvalidOwnerException e) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", e.getErrorCode().getHttpStatusCode());
		errorResponse.put("error", e.getErrorCode().getErrorCode());
		errorResponse.put("message", e.getErrorCode().getDescription());
		return ResponseEntity.status(e.getErrorCode().getHttpStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(PetNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handlePetNotFound(PetNotFoundException e) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", e.getErrorCode().getHttpStatusCode());
		errorResponse.put("error", e.getErrorCode().getErrorCode());
		errorResponse.put("message", e.getErrorCode().getDescription());
		return ResponseEntity.status(e.getErrorCode().getHttpStatusCode()).body(errorResponse);
	}
}
