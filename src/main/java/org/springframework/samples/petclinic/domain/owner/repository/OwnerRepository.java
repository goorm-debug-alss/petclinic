package org.springframework.samples.petclinic.domain.owner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.owner.model.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {
	Boolean existsByUserId(String userId);

	Optional<Owner> findByUserId(String userId);

	Optional<Owner> findById(Integer id);
}
