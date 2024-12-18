package org.springframework.samples.petclinic.domain.visit.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.samples.petclinic.domain.visit.service.VisitService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
class VisitController {
	private final VisitService visitService;
	/**
	 * 새로운 방문 내역 추가
	 *
	 * @param requestDto 요청 본문으로 전달된 방문 내역
	 * @return 추가된 방문 내역
	 */
	@PostMapping
	public ResponseEntity<VisitResponseDto> createVisit(@RequestBody VisitRequestDto requestDto) {
		VisitResponseDto response = visitService.createVisit(requestDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}


	/**
	 * 특정 반려동물의 방문 내역 전체 조회
	 *
	 * @param petId 반려동물 ID
	 * @return 방문 내역 목록
	 */
	@GetMapping("/{petId}")
	public ResponseEntity<VisitResponseDto> getVisitsByPetId(@PathVariable int petId) {
		VisitResponseDto response = visitService.getVisitsByPetId(petId);
		return ResponseEntity.ok(response);
	}
}
