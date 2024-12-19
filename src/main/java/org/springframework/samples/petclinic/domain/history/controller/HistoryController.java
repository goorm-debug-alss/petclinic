package org.springframework.samples.petclinic.domain.history.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.service.HistoryService;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;
	/**
	 * 새로운 진료 내역 추가
	 *
	 * @param request 요청 본문으로 전달된 진료 내역
	 * @return 추가된 진료 내역
	 */
	@PostMapping
	public ResponseEntity<?> addHistory(@RequestBody HistoryRequestDto request) {
		HistoryResponseDto response = historyService.addHistory(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * 특정 반려동물의 진료 내역 전체 조회
	 *
	 * @param petId 반려동물 ID
	 * @return 방문 내역 목록
	 */
	@GetMapping("/{petId}")
	public ResponseEntity<HistoryResponseDto> getHistoriesByPetId(@PathVariable int petId) {
		HistoryResponseDto response = historyService.getHistoriesByPetId(petId);
		return ResponseEntity.ok(response);
	}

	/**
	 * 새로운 진료 내역 추가
	 *
	 * @param request 요청 본문으로 전달된 진료 내역
	 * @return 추가된 진료 내역
	 */
	@PutMapping("/{historyId}")
	public ResponseEntity<?> addHistory(@PathVariable int historyId,@RequestBody HistoryRequestDto request) {
		HistoryResponseDto response = historyService.updateHistory(historyId,request);
		return ResponseEntity.ok(response);
	}
}
