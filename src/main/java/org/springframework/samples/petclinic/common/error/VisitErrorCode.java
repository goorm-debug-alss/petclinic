package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum VisitErrorCode implements ErrorCodeInterface{
	NO_VISIT(HttpStatus.BAD_REQUEST.value(), 501, "해당 방문내역이 존재하지 않습니다."),
;

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;

}
