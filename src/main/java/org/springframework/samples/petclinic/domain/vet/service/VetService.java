package org.springframework.samples.petclinic.domain.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetSpecialtyRepository;
import org.springframework.samples.petclinic.domain.vet.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.convert.VetConvert;
import org.springframework.samples.petclinic.domain.vet.convert.VetSpecialtyConvert;
import org.springframework.samples.petclinic.domain.vet.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.VetSpeciality;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VetService {

	private final VetRepository vetRepository;
	private final VetConvert vetConvert;
	private final VetSpecialtyRepository vetSpecialtyRepository;
	private final SpecialityService specialityService;
	private final VetSpecialtyConvert vetSpecialtyConvert;

	// 수의사 등록
	@Transactional
	public VetResponseDto register(VetRequestDto vetRequestDto) {
		validateVetRequestDto(vetRequestDto);

		List<Specialty> validSpecialties = specialityService.findByIds(vetRequestDto.getSpecialties());

		Vet vet = vetConvert.toEntity(vetRequestDto);
		Vet savedVet = vetRepository.save(vet);

		saveSpecialities(savedVet, validSpecialties);
		return vetConvert.toResponse(savedVet);
	}


	// 수의사 전체 조회
	public List<VetResponseDto> findAll() {
		return vetRepository.findAllByOrderById().stream()
			.map(vetConvert::toResponse)
			.collect(Collectors.toList());
	}

	// 특정 수의사 조회
	public VetResponseDto findById(int vetId) {
		return vetRepository.findById(vetId)
			.map(vetConvert::toResponse)
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));
	}

	// 전문 분야별 수의사 조회
	public List<VetResponseDto> findBySpecialtyId(int specialtyId) {
		var vetIds = vetSpecialtyRepository.findVetIdsBySpecialtyId_Id(specialtyId)
			.stream()
			.map(vs -> vs.getVet().getId())
			.collect(Collectors.toList());

		if (vetIds.isEmpty()) {
			throw new ApiException(VetErrorCode.NO_SPECIALITY);
		}

		return Optional.of(vetRepository.findAllById(vetIds))
			.orElse(Collections.emptyList())
			.stream()
			.map(vetConvert::toResponse)
			.collect(Collectors.toList());
	}

	// 수의사 삭제
	@Transactional
	public void delete(int vetId) {
		Vet vet = vetRepository.findById(vetId)
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));

		vetSpecialtyRepository.deleteAllByVetId_Id(vetId);
		vetRepository.delete(vet);
	}

	// 수의사 수정
	@Transactional
	public VetResponseDto update(int id, VetRequestDto vetRequestDto) {
		Vet vet = vetRepository.findById(id)
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));

		// 이름 수정
		Optional.ofNullable(vetRequestDto.getName()).ifPresent(vet::setName);

		// 분야 수정
		if (vetRequestDto.getSpecialties() != null && !vetRequestDto.getSpecialties().isEmpty()) {
			List<Specialty> validSpecialties = specialityService.findByIds(vetRequestDto.getSpecialties());

			vetSpecialtyRepository.deleteAllByVetId_Id(vet.getId());
			saveSpecialities(vet, validSpecialties);
		}

		vetRepository.save(vet);
		return vetConvert.toResponse(vet);
	}

	// 전문분야-수의사 연결 테이블 저장
	private void saveSpecialities(Vet vet, List<Specialty> specialties) {
		List<VetSpeciality> vetSpecialties = vetSpecialtyConvert.toEntityList(vet, specialties);
		vetSpecialtyRepository.saveAll(vetSpecialties);
	}

	// 요청값 검증
	private void validateVetRequestDto(VetRequestDto vetRequestDto) {
		if (vetRequestDto.getName() == null || vetRequestDto.getName().isBlank()) {
			throw new ApiException(VetErrorCode.NULL_NAME);
		}
		if (vetRequestDto.getSpecialties() == null || vetRequestDto.getSpecialties().isEmpty()) {
			throw new ApiException(VetErrorCode.NULL_SPECIALITY);
		}
	}
}
