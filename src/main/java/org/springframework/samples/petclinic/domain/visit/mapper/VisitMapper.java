package org.springframework.samples.petclinic.domain.visit.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.pet.mapper.PetMapper;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.stereotype.Component;

@Component
public class VisitMapper {

	@Autowired
	private PetRepository petRepository;

	/**
	 * Visit 엔티티 생성
	 *
	 * @param requestDto 요청 DTO
	 * @return 생성된 Visit 객체
	 */
	public Visit toEntity(VisitRequestDto requestDto, Pet pet) {
		return Visit.builder()
			.description(requestDto.getDescription())
			.visitDate(requestDto.getVisitDate())
			.petId(pet)
			.build();
	}

	/**
	 * Visit 객체를 기반으로 응답 DTO 생성
	 *
	 * @param visit 저장된 Visit 객체
	 * @return VisitResponseDto 반환
	 */
	public VisitResponseDto toDto(Visit visit) {

		return VisitResponseDto.builder()
			.visitId(visit.getId())
			.visitDate(visit.getVisitDate())
			.description(visit.getDescription())
			.petName(visit.getPetId().getName())
			.build();
	}

}
