package org.springframework.samples.petclinic.domain.visit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequestDto {

	@NotNull(message = "PetID는 필수입니다.")
	private int petId;

	@NotNull(message = "방문일시는 필수입니다.")
	@PastOrPresent(message = "방문일시는 현재보다 미래일 수 없습니다.")
	private LocalDateTime visitDate;

	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 255, message = "내용은 255byte를 초과할 수 없습니다.")
	private String description;
}
