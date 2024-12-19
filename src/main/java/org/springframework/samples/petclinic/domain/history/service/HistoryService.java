package org.springframework.samples.petclinic.domain.history.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.repository.HistoryRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.history.enums.StatusCode;
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
	private final VetRepository vetRepository;
	private final VisitRepository visitRepository;
	private final PetRepository petRepository;


	/**
	 * 진료 내역 생성
	 *
	 * @param requestDto 진료 요청 데이터
	 * @return HistoryResponseDto 저장된 진료 정보 반환
	 */
	public HistoryResponseDto addHistory(HistoryRequestDto requestDto) {

		History history = createHistoryEntity(requestDto);
		History savedHistory = historyRepository.save(history);

		return buildResponseDto(savedHistory);
	}


	/**
	 * History 엔티티 생성
	 *
	 * @param requestDto 요청 DTO
	 * @return 생성된 History 객체
	 */
	private History createHistoryEntity(HistoryRequestDto requestDto) {
		Vet vet = vetRepository.findById(requestDto.getVetId())
			.orElseThrow(() -> new IllegalArgumentException("Vet not found"));
		Visit visit = visitRepository.findById(requestDto.getVisitId())
			.orElseThrow(() -> new IllegalArgumentException("Visit not found"));

		return History.builder()
			.symptoms(requestDto.getSymptoms())
			.content(requestDto.getContent())
			.vetId(vet)
			.visitId(visit)
			.build();
	}

	/**
	 * History 객체를 기반으로 응답 DTO 생성
	 *
	 * @param history 저장된 History 객체
	 * @return HistoryResponseDto 반환
	 */
	private HistoryResponseDto buildResponseDto(History history) {

		HistoryResponseDto.Result result = HistoryResponseDto.Result.builder()
			.resultCode(StatusCode.SUCCESS.getCode())
			.resultDescription(StatusCode.SUCCESS.getDescription())
			.build();

		HistoryResponseDto.Body body = HistoryResponseDto.Body.builder()
			.historyId(history.getId())
			.symptoms(history.getSymptoms())
			.content(history.getContent())
			.vetId(history.getVetId().getId())
			.visitId(history.getVisitId().getId())
			.build();

		return HistoryResponseDto.builder()
			.result(result)
			.body(List.of(body))
			.build();

	}

	/**
	 * 특정 반려동물의 진료 내역 전체 조회
	 *
	 * @param petId 반려동물 ID
	 * @return 진료 내역 목록 응답 DTO
	 */
	public HistoryResponseDto getHistoriesByPetId(int petId) {

		Pet pet = petRepository.findById(petId)
			.orElseThrow(() -> new IllegalArgumentException("Pet not found"));

		List<HistoryResponseDto.Body> histories = historyRepository.findAllByVisitId_PetId(pet)
			.stream()
			.map(history -> HistoryResponseDto.Body.builder()
				.historyId(history.getId())
				.symptoms(history.getSymptoms())
				.content(history.getContent())
				.vetId(history.getVetId().getId())
				.visitId(history.getVisitId().getId())
				.build())
			.collect(Collectors.toList());

		return HistoryResponseDto.builder()
			.result(HistoryResponseDto.Result.builder()
				.resultCode(StatusCode.SUCCESS.getCode())
				.resultDescription(StatusCode.SUCCESS.getDescription())
				.build())
			.body(histories)
			.build();
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
			.orElseThrow(() -> new IllegalArgumentException("History not found"));

		Vet vet = vetRepository.findById(request.getVetId())
			.orElseThrow(() -> new IllegalArgumentException("Vet not found"));

		Visit visit = visitRepository.findById(request.getVisitId())
			.orElseThrow(() -> new IllegalArgumentException("Visit not found"));

		// 업데이트 내용 반영
		history.setSymptoms(request.getSymptoms());
		history.setContent(request.getContent());
		history.setVetId(vet);
		history.setVisitId(visit);

		//업데이트 내용 저장
		History updateEntity = historyRepository.save(history);

		//응답 DTO 생성
		HistoryResponseDto historyResponseDto = buildResponseDto(updateEntity);

		return historyResponseDto;
	}

	/**
	 * 진료 내역 삭제
	 *
	 * @param historyId 삭제할 진료 내역 ID
	 */
	public void deleteHistory(int historyId) {
		if (!historyRepository.existsById(historyId)) {
			throw new IllegalArgumentException("History not found with id: " + historyId);
		}

		historyRepository.deleteById(historyId);
	}
}
