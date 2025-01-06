package org.springframework.samples.petclinic.domain.vet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.vet.model.Specialty;

import java.util.Optional;

public interface SpecialityRepository extends JpaRepository<Specialty, Integer> {
	Optional<Specialty> findByName(String name);
}