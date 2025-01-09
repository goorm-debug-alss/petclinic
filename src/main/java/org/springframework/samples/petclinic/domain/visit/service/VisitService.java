package org.springframework.samples.petclinic.domain.visit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.samples.petclinic.domain.visit.mapper.VisitMapper;
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
	private final VisitMapper visitMapper;

    /**
     * 방문 내역 생성
     *
     * @param requestDto 방문 요청 데이터
     * @return VisitResponseDto 저장된 방문 정보 반환
     */
    public VisitResponseDto createVisit(VisitRequestDto requestDto) {
		Pet pet = petRepository.findById(requestDto.getPetId())
			.orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));

		Visit visit = visitMapper.toEntity(requestDto, pet);
        Visit savedVisit = visitRepository.save(visit);
        return visitMapper.toDto(savedVisit);
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
                .map(visitMapper::toDto)
                .collect(Collectors.toList());
    }
}
