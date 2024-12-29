package org.springframework.samples.petclinic.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.common.result.ErrorResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice(basePackages = {"org.springframework.samples.petclinic.domain"})
public class GlobalExceptionHandler {

	// ApiException 처리
	@ExceptionHandler(value = ApiException.class)
	public ResponseEntity<ErrorResult> handleApiException(ApiException apiException) {
		log.error("Handled ApiException: {}", apiException.getMessage(), apiException);
		var errorCode = apiException.getErrorCodeInterface();

		return ResponseEntity
			.status(errorCode.getHttpStatusCode())
			.body(ErrorResult.ERROR(errorCode.getErrorCode(), apiException.getErrorDescription()));
	}

	// 그 외 예외 처리
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ErrorResult> handleGeneralException(Exception exception) {
		log.error("Unhandled Exception: {}", exception.getMessage(), exception);
		return ResponseEntity
			.status(500)
			.body(ErrorResult.ERROR(500, "서버 내부 오류"));
	}
}
