package org.springframework.samples.petclinic.domain.vet.mapper;

import org.springframework.samples.petclinic.domain.vet.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.VetSpeciality;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VetSpecialtyMapper {

	public List<VetSpeciality> toEntityList(Vet vet, List<Specialty> specialties) {
		return specialties.stream()
			.map(specialty -> VetSpeciality.builder()
				.vet(vet)
				.specialty(specialty)
				.build())
			.collect(Collectors.toList());
	}
}
