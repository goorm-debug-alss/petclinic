package org.springframework.samples.petclinic.domain.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.vet.SpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.VetSpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.model.VetSpeciality;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SpecialityService {

	private final VetSpecialityRepository vetSpecialityRepository;
	private final SpecialityRepository specialityRepository;

	public List<Specialty> find(int vetId) {
		List<VetSpeciality> vetSpecialties = vetSpecialityRepository.findVetSpecialtiesByVetId_Id(vetId);

		return vetSpecialties.stream()
			.map(VetSpeciality::getSpecialty)
			.toList();
	}

	public List<Specialty> findByIds(List<Integer> specialtyIds) {
		return specialtyIds.stream()
			.map(specialtyId -> specialityRepository.findById(specialtyId)
				.orElseThrow(() -> new RuntimeException("Specialty not found for ID: " + specialtyId)))
			.collect(Collectors.toList());
	}
}
