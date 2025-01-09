package org.springframework.samples.petclinic.domain.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.vet.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetSpecialtyRepository;
import org.springframework.samples.petclinic.domain.vet.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.model.VetSpeciality;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SpecialtyService {

	private final VetSpecialtyRepository vetSpecialtyRepository;
	private final SpecialtyRepository specialtyRepository;

	public List<Specialty> find(int vetId) {
		List<VetSpeciality> vetSpecialties = vetSpecialtyRepository.findVetSpecialtiesByVetId_Id(vetId);

		return vetSpecialties.stream()
			.map(VetSpeciality::getSpecialty)
			.toList();
	}

	public List<Specialty> findByIds(List<Integer> specialtyIds) {
		return specialtyIds.stream()
			.map(specialtyId -> specialtyRepository.findById(specialtyId)
				.orElseThrow(() -> new ApiException(VetErrorCode.NO_SPECIALITY)))
			.collect(Collectors.toList());
	}
}
