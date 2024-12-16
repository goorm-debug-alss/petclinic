package org.springframework.samples.petclinic.domain.appointment.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ApptStatus {
	COMPLETE("완료"),

	CANCEL("취소"),

	;

	private String description;
}
