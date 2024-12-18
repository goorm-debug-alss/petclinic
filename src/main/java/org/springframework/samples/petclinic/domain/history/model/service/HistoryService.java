package org.springframework.samples.petclinic.domain.history.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbagePetRepository;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.history.model.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.model.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.model.repository.HistoryRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryRepository historyRepository;
	private final VetRepository vetRepository;
   // private final VisitRepository visitRepository;

//	public HistoryResponseDto addHistory(HistoryRequestDto request) {
//		// History 객체 생성 및 저장
//		History history = History.builder()
//			.symptoms(request.getSymptoms())
//			.content(request.getContent())
//			.vetId(historyRepository.findById(request.getVetId()) // Vet ID 설정
//			.visitId(visitRepository.findById(request.getVisitId())) // Visit ID 설정
//			.build();
//
//		History savedHistory = historyRepository.save(history);
//
//		// Response 생성
//		return new HistoryResponseDto(
//			savedHistory.getId(),
//			savedHistory.getSymptoms(),
//			savedHistory.getContent(),
//			request.getVetId(),
//			request.getVisitId()
//		);
//	}
}
