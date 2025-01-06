package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
public enum HistoryErrorCode implements ErrorCodeInterface{

		NO_HISTORY(HttpStatus.BAD_REQUEST.value(), 801, "해당 진료내역이 존재하지 않습니다."),
		;

		private final Integer httpStatusCode;
		private final Integer errorCode;
		private final String description;

	}
