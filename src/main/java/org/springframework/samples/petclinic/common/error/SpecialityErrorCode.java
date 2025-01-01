package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SpecialityErrorCode implements ErrorCodeInterface {

	NO_SPECIALITY(HttpStatus.BAD_REQUEST.value(), 200, "해당 전공분야를 찾을 수 없습니다.");

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;

}
