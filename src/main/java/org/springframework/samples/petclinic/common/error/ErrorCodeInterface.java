package org.springframework.samples.petclinic.common.error;

public interface ErrorCodeInterface {
	Integer getHttpStatusCode();
	Integer getErrorCode();
	String getDescription();
}
