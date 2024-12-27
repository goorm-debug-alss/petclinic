package org.springframework.samples.petclinic.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResult {

	private Integer errorCode;
	private String description;

	// 오류 응답 생성
	public static ErrorResult ERROR(Integer errorCode, String description) {
		return new ErrorResult(errorCode, description);
	}
}
