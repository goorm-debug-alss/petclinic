package org.springframework.samples.petclinic.domain.visit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.samples.petclinic.domain.visit.model.Visit;

import org.springframework.samples.petclinic.domain.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;
    private final PetRepository petRepository;

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
    private Visit createVisitEntity(VisitRequestDto requestDto) {

        Pet pet = petRepository.findById(requestDto.getPetId())
                .orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));

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

        return VisitResponseDto.builder()
                .visitId(visit.getId())
                .visitDate(visit.getVisitDate())
                .description(visit.getDescription())
                .petName(visit.getPetId().getName())
                .build();
    }

    /**
     * 특정 반려동물의 방문 내역 전체 조회
     *
     * @param petId 반려동물 ID
     * @return 방문 내역 목록 응답 DTO
     */
    public List<VisitResponseDto> getVisitsByPetId(int petId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));

        return visitRepository.findAllByPetId(pet)
                .stream()
                .map(visit -> VisitResponseDto.builder()
                        .visitId(visit.getId())
                        .petName(pet.getName())
                        .visitDate(visit.getVisitDate())
                        .description(visit.getDescription())
                        .build())
                .collect(Collectors.toList());

    }
}
