package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
public enum VetErrorCode implements ErrorCodeInterface {

	NO_VET(HttpStatus.BAD_REQUEST.value(), 201, "해당 수의사가 존재하지 않습니다."),
	NO_SPECIALITY(HttpStatus.BAD_REQUEST.value(), 202, "해당 전공분야가 존재하지 않습니다."),
	NULL_NAME(HttpStatus.BAD_REQUEST.value(), 203, "이름은 필수값 입니다."),
	NULL_SPECIALITY(HttpStatus.BAD_REQUEST.value(), 204, "전공분야는 필수값 입니다."),
	NULL_DTO(HttpStatus.BAD_REQUEST.value(), 205, "DTO가 NULL 입니다.");

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;
}
