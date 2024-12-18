package org.springframework.samples.petclinic.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.pet.model.PetType;

public interface PetTypeRepository extends JpaRepository<PetType, Integer> {
}
