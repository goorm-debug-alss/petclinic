package org.springframework.samples.petclinic.domain.owner;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.pet.model.PetType;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	List<PetType> findPetTypes();

	Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable);

	Optional<Owner> findById(@Nonnull Integer id);

	Page<Owner> findAll(Pageable pageable);

}
