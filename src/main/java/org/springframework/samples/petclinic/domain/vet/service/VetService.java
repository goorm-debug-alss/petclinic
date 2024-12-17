package org.springframework.samples.petclinic.domain.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.speciality.SpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.VetSpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.convert.VetConvert;
import org.springframework.samples.petclinic.speciality.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.VetSpecialty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

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
}
