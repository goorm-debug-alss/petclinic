package org.springframework.samples.petclinic.domain.history.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequestDto {

	private String symptoms;
	private String content;
	private int vetId;
	private int visitId;
}

