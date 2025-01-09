package org.springframework.samples.petclinic.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.pet.enums.PetStatus;
import org.springframework.samples.petclinic.domain.pet.model.Pet;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Integer> {
	List<Pet> findAllByStatusOrderById(PetStatus status);

	Optional<Pet> findByIdAndStatus(Integer id, PetStatus status);
}
