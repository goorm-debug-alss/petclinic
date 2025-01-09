package org.springframework.samples.petclinic.domain.history.mapper;

import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.stereotype.Component;

@Component
public class HistoryMapper {

	/**
	 * History 객체를 기반으로 응답 DTO 생성
	 *
	 * @param history 저장된 History 객체
	 * @return HistoryResponseDto 반환
	 */
	public HistoryResponseDto toDto(History history) {
		return HistoryResponseDto.builder()
			.historyId(history.getId())
			.symptoms(history.getSymptoms())
			.content(history.getContent())
			.vetId(history.getVetId().getId())
			.visitId(history.getVisitId().getId())
			.build();
	}

	/**
	 * History 엔티티 생성
	 *
	 * @param requestDto 요청 DTO
	 * @param vet        유효성 검증된 Vet 객체
	 * @param visit      유효성 검증된 Visit 객체
	 * @return 생성된 History 객체
	 */
	public History toEntity(HistoryRequestDto requestDto, Vet vet, Visit visit) {
		return History.builder()
			.symptoms(requestDto.getSymptoms())
			.content(requestDto.getContent())
			.vetId(vet)
			.visitId(visit)
			.build();
	}
}
