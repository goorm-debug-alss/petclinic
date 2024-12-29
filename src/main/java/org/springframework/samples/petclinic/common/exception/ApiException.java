package org.springframework.samples.petclinic.common.exception;

import lombok.Getter;
import org.springframework.samples.petclinic.common.error.ErrorCodeInterface;

@Getter
public class ApiException extends RuntimeException implements ApiExceptionInterface {

	private final ErrorCodeInterface errorCodeInterface;
	private final String errorDescription;

	public ApiException(ErrorCodeInterface errorCodeInterface) {
		super(errorCodeInterface.getDescription());
		this.errorCodeInterface = errorCodeInterface;
		this.errorDescription = errorCodeInterface.getDescription();
	}
}
