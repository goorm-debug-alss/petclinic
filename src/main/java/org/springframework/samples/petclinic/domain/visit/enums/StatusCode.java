package org.springframework.samples.petclinic.domain.visit.enums;

import lombok.Getter;

@Getter
public enum StatusCode {

	SUCCESS("200", "Operation completed successfully"),
	NOT_FOUND("404", "Resource not found"),
	SERVER_ERROR("500", "Internal server error");

	private final String code;
	private final String description;

	StatusCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
}
