package org.springframework.samples.petclinic.common.exception;

import org.springframework.samples.petclinic.common.error.ErrorCodeInterface;

public interface ApiExceptionInterface{

	ErrorCodeInterface getErrorCodeInterface();

	String getErrorDescription();

}
