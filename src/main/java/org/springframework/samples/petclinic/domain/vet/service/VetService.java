package org.springframework.samples.petclinic.domain.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.speciality.SpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.VetSpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.convert.VetConvert;
import org.springframework.samples.petclinic.domain.speciality.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.VetSpecialty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VetService {

	private final VetRepository vetRepository;
	private final SpecialityRepository specialityRepository;
	private final VetConvert vetConvert;
	private final VetSpecialityRepository vetSpecialityRepository;

	// 수의사 등록
	@Transactional
	public VetResponseDto register(VetRequestDto vetRequestDto) {

		if (vetRequestDto == null) {
			throw new RuntimeException("요청데이터가 비어 있습니다.");
		}

		if (vetRequestDto.getName() == null || vetRequestDto.getName().isBlank()) {
			throw new ApiException(VetErrorCode.NULL_NAME);
		}

		if (vetRequestDto.getSpecialties() == null || vetRequestDto.getSpecialties().isEmpty()) {
			throw new ApiException(VetErrorCode.NO_SPECIALITY);
		}

		Vet vet = new Vet();
		vet.setName(vetRequestDto.getName());
		Vet savedVet = vetRepository.save(vet);

		Set<VetSpecialty> vetSpecialties = new HashSet<>();

		// 리스트 처리
		vetRequestDto.getSpecialties().forEach(specialtyDto -> {
			// Specialty 확인 후 저장 또는 조회
			Specialty specialty = specialityRepository.findByName(specialtyDto.getName())
				.orElseGet(() -> {
					Specialty newSpecialty = new Specialty();
					newSpecialty.setName(specialtyDto.getName());
					return specialityRepository.save(newSpecialty);
				});

			// VetSpecialty 생성 및 연결
			VetSpecialty vetSpecialty = new VetSpecialty();
			vetSpecialty.setVetId(savedVet);
			vetSpecialty.setSpecialtyId(specialty);
			vetSpecialityRepository.save(vetSpecialty);

			vetSpecialties.add(vetSpecialty);
		});

		return vetConvert.toResponse(savedVet);
	}

	// 수의사 전체 조회
	public List<VetResponseDto> findAll() {
		var vetList = vetRepository.findAllByOrderById();
		return Optional.ofNullable(vetList)
			.orElse(Collections.emptyList())
			.stream()
			.map(vetConvert::toResponse)
			.collect(Collectors.toList());
	}

	// 특정 수의사 조회
	public VetResponseDto findById(int vetId) {
		var vet = vetRepository.findById(vetId)
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));
		return vetConvert.toResponse(vet);
	}

	// 전문 분야별 수의사 조회
	public List<VetResponseDto> findBySpecialtyId(int specialtyId) {
		var vetSpecialties = vetSpecialityRepository.findVetIdsBySpecialtyId_Id(specialtyId);
		var vetIds = vetSpecialties.stream()
			.map(vs -> vs.getVetId().getId())
			.collect(Collectors.toList());
		var vetList = vetRepository.findAllById(vetIds);
		return Optional.of(vetList)
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

		vetSpecialityRepository.deleteAllByVetId_Id(vetId);
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
		Optional.ofNullable(vetRequestDto.getSpecialties()).ifPresent(specialtyDtos -> {
			vetSpecialityRepository.deleteAllByVetId_Id(vet.getId());

			specialtyDtos.forEach(specialtyDto -> {
				Specialty specialty = specialityRepository.findByName(specialtyDto.getName())
					.orElseGet(() -> {
						Specialty newSpecialty = new Specialty();
						newSpecialty.setName(specialtyDto.getName());
						return specialityRepository.save(newSpecialty);
					});

				// VetSpecialty 생성 / 연결
				VetSpecialty vetSpecialty = new VetSpecialty();
				vetSpecialty.setVetId(vet);
				vetSpecialty.setSpecialtyId(specialty);
				vetSpecialityRepository.save(vetSpecialty);
			});
		});
		vetRepository.save(vet);
		return vetConvert.toResponse(vet);
	}
}
