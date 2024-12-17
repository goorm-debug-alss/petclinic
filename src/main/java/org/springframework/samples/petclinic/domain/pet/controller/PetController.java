package org.springframework.samples.petclinic.domain.pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

	private final PetService petService;

	// 모든 Pet 조회
	@GetMapping
	public ResponseEntity<List<PetResponseDto>> getAllPets() {
		return ResponseEntity.ok(petService.getAllPets());
	}

	// 단일 Pet 조회
	@GetMapping("/{petId}")
	public ResponseEntity<PetResponseDto> getPetById(@PathVariable Long petId) {
		return ResponseEntity.ok(petService.getPetById(petId));
	}

	// Pet 생성
	@PostMapping
	public ResponseEntity<PetResponseDto> createPet(@RequestBody PetRequestDto request) {
		return ResponseEntity.ok(petService.createPet(request));
	}

	// Pet 수정
	@PutMapping("/{petId}")
	public ResponseEntity<PetResponseDto> updatePet(@PathVariable Long petId, @RequestBody PetRequestDto request) {
		return ResponseEntity.ok(petService.updatePet(petId, request));
	}

	// Pet 삭제
	@DeleteMapping("/{petId}")
	public ResponseEntity<Void> deletePet(@PathVariable Long petId) {
		petService.deletePet(petId);
		return ResponseEntity.noContent().build();
	}
}
