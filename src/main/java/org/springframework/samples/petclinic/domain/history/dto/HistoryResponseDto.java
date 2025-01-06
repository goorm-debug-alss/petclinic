package org.springframework.samples.petclinic.domain.history.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoryResponseDto {

		private int historyId;
		private String symptoms;
		private String content;
		private int vetId;
		private int visitId;
}
