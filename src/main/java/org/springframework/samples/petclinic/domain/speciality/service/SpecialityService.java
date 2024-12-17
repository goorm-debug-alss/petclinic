package org.springframework.samples.petclinic.domain.speciality.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.vet.VetSpecialityRepository;
import org.springframework.samples.petclinic.domain.speciality.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.model.VetSpecialty;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SpecialityService {


	private final VetSpecialityRepository vetSpecialityRepository;

	public List<Specialty> find(int vetId) {
		List<VetSpecialty> vetSpecialties = vetSpecialityRepository.findVetSpecialtiesByVetId_Id(vetId);

		return vetSpecialties.stream()
			.map(VetSpecialty::getSpecialtyId)
			.toList();
	}
}
