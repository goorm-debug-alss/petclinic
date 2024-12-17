package org.springframework.samples.petclinic.domain.vet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.vet.model.VetSpecialty;

import java.util.List;

public interface VetSpecialityRepository extends JpaRepository<VetSpecialty, Integer> {
	List<VetSpecialty> findVetSpecialtiesByVetId_Id(int vetId);
}
