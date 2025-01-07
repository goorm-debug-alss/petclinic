package org.springframework.samples.petclinic.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.common.result.ErrorResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

	// 검증 예외 처리
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResult> handleValidationException(MethodArgumentNotValidException exception) {
		log.error("Validation Error: {}", exception.getMessage(), exception);

		var fieldError = exception.getBindingResult().getFieldError();
		String errorMessage = (fieldError != null) ? fieldError.getDefaultMessage() : "요청 데이터 검증 오류";

		return ResponseEntity
			.status(400)
			.body(ErrorResult.ERROR(400, errorMessage));
	}

	// 잘못된 요청 형식
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResult> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		log.error("Malformed JSON request: {}", exception.getMessage(), exception);

		return ResponseEntity
			.status(400)
			.body(ErrorResult.ERROR(400, "잘못된 요청"));
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
