package org.springframework.samples.petclinic.domain.visit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.samples.petclinic.domain.visit.enums.StatusCode;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.samples.petclinic.domain.visit.tempRepository.TempPetRepository;
import org.springframework.samples.petclinic.domain.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitService {

	private final VisitRepository visitRepository;
	private final TempPetRepository petRepository; //테스트용 임시저장소

	/**
	 * 방문 내역 생성
	 *
	 * @param requestDto 방문 요청 데이터
	 * @return VisitResponseDto 저장된 방문 정보 반환
	 */
	public VisitResponseDto createVisit(VisitRequestDto requestDto) {

		Visit visit = createVisitEntity(requestDto);
		Visit savedVisit = visitRepository.save(visit);
		return buildResponseDto(savedVisit);
	}

	/**
	 * Visit 엔티티 생성
	 *
	 * @param requestDto 요청 DTO
	 * @return 생성된 Visit 객체
	 */
	private Visit createVisitEntity(VisitRequestDto requestDto) throws IllegalArgumentException {

		Pet pet = petRepository.findById(requestDto.getPetId())
			.orElseThrow(() -> new IllegalArgumentException("Pet not found."));

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
	private VisitResponseDto buildResponseDto(Visit visit) {

		VisitResponseDto.Result result = VisitResponseDto.Result.builder()
			.resultCode(StatusCode.SUCCESS.getCode())
			.resultDescription(StatusCode.SUCCESS.getDescription())
			.build();

		VisitResponseDto.Body body = VisitResponseDto.Body.builder()
			.petId(visit.getPetId().getId())
			.visitDate(visit.getVisitDate())
			.description(visit.getDescription())
			.petName(visit.getPetId().getName())
			.build();

		return VisitResponseDto.builder()
			.result(result)
			.body(body)
			.build();
	}
}
