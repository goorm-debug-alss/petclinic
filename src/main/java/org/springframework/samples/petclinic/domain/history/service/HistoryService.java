package org.springframework.samples.petclinic.domain.history.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.HistoryErrorCode;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.error.VisitErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.history.mapper.HistoryMapper;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.repository.HistoryRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.service.VetService;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.samples.petclinic.domain.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class HistoryService {

	private final HistoryRepository historyRepository;
	private final PetRepository petRepository;
	private final VetService vetService;
	private final VisitRepository visitRepository;
	private final HistoryMapper historyMapper;

	/**
	 * 진료 내역 생성
	 *
	 * @param requestDto 진료 요청 데이터
	 * @return HistoryResponseDto 저장된 진료 정보 반환
	 */
	public HistoryResponseDto addHistory(HistoryRequestDto requestDto) {
		Vet vet = vetService.getVetOrThrow(requestDto.getVetId());
		Visit visit = visitRepository.findById(requestDto.getVisitId())
			.orElseThrow(() -> new ApiException(VisitErrorCode.NO_VISIT));

		History history = historyMapper.toEntity(requestDto, vet, visit);
		History savedHistory = historyRepository.save(history);

		return historyMapper.toDto(savedHistory);
	}


	/**
	 * 특정 반려동물의 진료 내역 전체 조회
	 *
	 * @param petId 반려동물 ID
	 * @return 진료 내역 목록 응답 DTO
	 */
	public List<HistoryResponseDto> getHistoriesByPetId(int petId) {

		Pet pet = petRepository.findById(petId)
			.orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));

		return historyRepository.findAllByVisitId_PetId(pet)
			.stream()
			.map(historyMapper::toDto)
			.collect(Collectors.toList());
	}

	/**
	 * 진료 내역 수정
	 *
	 * @param historyId 수정할 진료내역 ID
	 * @param request 수정할 내역 요청 데이터
	 * @return HistoryResponseDto 수정된 진료 정보 반환
	 */
	public HistoryResponseDto updateHistory(int historyId, HistoryRequestDto request) {
		// 존재 여부 확인
		History history = historyRepository.findById(historyId)
			.orElseThrow(() -> new ApiException(HistoryErrorCode.NO_HISTORY));

		Vet vet = vetService.getVetOrThrow(request.getVetId());

		Visit visit = visitRepository.findById(request.getVisitId())
			.orElseThrow(() -> new ApiException(VisitErrorCode.NO_VISIT));

		// 업데이트 내용 반영
		history.setSymptoms(request.getSymptoms());
		history.setContent(request.getContent());
		history.setVet(vet);
		history.setVisit(visit);

		//업데이트 내용 저장
		History updateEntity = historyRepository.save(history);

		//응답 DTO 생성
		HistoryResponseDto historyResponseDto = historyMapper.toDto(updateEntity);

		return historyResponseDto;
	}

	/**
	 * 진료 내역 삭제
	 *
	 * @param historyId 삭제할 진료 내역 ID
	 */
	public void deleteHistory(int historyId) {

		//진료내역 존재 여부 확인
		if (!historyRepository.existsById(historyId)) {
			throw new ApiException(HistoryErrorCode.NO_HISTORY);
		}

		//진료내역 삭제
		historyRepository.deleteById(historyId);
	}
}
