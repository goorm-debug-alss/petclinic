package org.springframework.samples.petclinic.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AppointmentErrorCode implements ErrorCodeInterface {

	NO_APPOINTMENT(HttpStatus.BAD_REQUEST.value(), 301, "해당 예약이 존재하지 않습니다."),
	OUTSIDE_WORKING_HOURS(HttpStatus.BAD_REQUEST.value(), 302, "예약 시간이 영업 시간 범위를 벗어났습니다."),
	NULL_APPOINTMENT_DATE(HttpStatus.BAD_REQUEST.value(), 303, "예약 날짜는 필수값입니다."),
	INVALID_APPOINTMENT_DATE(HttpStatus.BAD_REQUEST.value(), 304, "예약 날짜가 과거일 수 없습니다."),
	INVALID_SYMPTOMS(HttpStatus.BAD_REQUEST.value(), 305, "증상은 필수값입니다."),
	NULL_APPOINTMENT_STATUS(HttpStatus.BAD_REQUEST.value(), 306, "예약 상태는 필수값입니다."),
	CONFLICTING_APPOINTMENT(HttpStatus.CONFLICT.value(), 307, "중복된 예약이 존재합니다.");

	private final Integer httpStatusCode;
	private final Integer errorCode;
	private final String description;
}
