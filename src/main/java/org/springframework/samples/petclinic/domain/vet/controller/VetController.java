package org.springframework.samples.petclinic.domain.vet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.vet.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.service.VetService;
import org.springframework.samples.petclinic.domain.vet.dto.VetResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vets")
public class VetController {

	private final VetService vetService;

	// 수의사 등록
	@PostMapping
	public ResponseEntity<VetResponseDto> create(@Valid @RequestBody VetRequestDto vetRequestDto) {
		var response = vetService.register(vetRequestDto);
		return ResponseEntity.ok(response);
	}

	// 전체 수의사 조회
	@GetMapping("/all")
	public ResponseEntity<List<VetResponseDto>> getAll() {
		var response = vetService.findAll();
		return ResponseEntity.ok(response);
	}

	// 특정 수의사 조회
	@GetMapping("/{vetId}")
	public ResponseEntity<VetResponseDto> getVet(@PathVariable("vetId") int vetId) {
		var response = vetService.findById(vetId);
		return ResponseEntity.ok(response);
	}

	// 분야별 수의사 조회
	@GetMapping
	public ResponseEntity<List<VetResponseDto>> getVetsBySpecialityId(
		@RequestParam(value = "speciality") int specialityId
	) {
		var response = vetService.findBySpecialtyId(specialityId);
		return ResponseEntity.ok(response);
	}

	// 수의사 수정
	@PutMapping("/{vetId}")
	public ResponseEntity<VetResponseDto> update(
			@PathVariable("vetId") int vetId, @RequestBody VetRequestDto vetRequestDto){
		var response = vetService.update(vetId, vetRequestDto);
		return ResponseEntity.ok(response);
	}

	// 수의사 삭제
	@DeleteMapping("/{vetId}")
	public ResponseEntity<Void> delete(@PathVariable("vetId") int vetId) {
		vetService.delete(vetId);
		return ResponseEntity.noContent().build();
	}
}
