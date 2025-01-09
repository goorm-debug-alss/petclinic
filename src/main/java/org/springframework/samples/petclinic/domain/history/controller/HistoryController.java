package org.springframework.samples.petclinic.domain.history.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.service.HistoryService;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	public ResponseEntity<?> addHistory(@Valid @RequestBody HistoryRequestDto request) {
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
	public ResponseEntity<List<HistoryResponseDto>> getHistoriesByPetId(@PathVariable("petId") int petId) {
		List<HistoryResponseDto> response = historyService.getHistoriesByPetId(petId);
		return ResponseEntity.ok(response);
	}

	/**
	 * 진료 내역 수정
	 *
	 * @param request 수정할 진료내역 요청 DTO
	 * @return 수정된 진료 내역
	 */
	@PutMapping("/{historyId}")
	public ResponseEntity<?> updateHistory(@PathVariable("historyId") int historyId,@Valid @RequestBody HistoryRequestDto request) {
		HistoryResponseDto response = historyService.updateHistory(historyId,request);
		return ResponseEntity.ok(response);
	}

	/**
	 * 진료 내역 삭제
	 *
	 * @param historyId 삭제할 진료 내역 ID
	 * @return 삭제 결과 메시지
	 */
	@DeleteMapping("/{historyId}")
	public ResponseEntity<?> deleteHistory(@PathVariable("historyId") int historyId) {
		historyService.deleteHistory(historyId);
		return ResponseEntity.ok("History deleted successfully.");
	}

}
