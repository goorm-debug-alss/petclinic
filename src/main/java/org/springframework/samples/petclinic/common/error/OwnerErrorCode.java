package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum OwnerErrorCode implements ErrorCodeInterface{

	NO_OWNER(HttpStatus.BAD_REQUEST.value(), 601, "해당 유저가 존재하지 않습니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), 602, "비밀번호가 일치하지 않습니다.");

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;
}
