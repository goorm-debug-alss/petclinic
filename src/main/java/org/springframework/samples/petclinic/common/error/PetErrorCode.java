package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PetErrorCode implements ErrorCodeInterface {

	NO_PET(HttpStatus.BAD_REQUEST.value(), 401, "해당 반려동물이 존재하지 않습니다.");

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;
}
