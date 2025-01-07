package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PetErrorCode implements ErrorCodeInterface {

	NO_PET(HttpStatus.NOT_FOUND.value(), 401, "해당 반려동물이 존재하지 않습니다."),
	INVALID_PET_TYPE(HttpStatus.BAD_REQUEST.value(), 402, "유효하지 않은 PetType ID입니다."),
	INVALID_OWNER(HttpStatus.BAD_REQUEST.value(), 403, "유효하지 않은 Owner ID입니다.");

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;
}
