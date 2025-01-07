package org.springframework.samples.petclinic.domain.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequestDto {

	@NotBlank(message = "증상은 필수입니다.")
	@Size(max = 255, message = "증상은 255byte를 초과할 수 없습니다.")
	private String symptoms;

	@NotBlank(message = "진료내용은 필수입니다.")
	@Size(max = 255, message = "진료내용은 255byte를 초과할 수 없습니다.")
	private String content;

	@NotNull(message = "vetID는 필수입니다.")
	private int vetId;
	@NotNull(message = "VisitID는 필수입니다.")
	private int visitId;
}

