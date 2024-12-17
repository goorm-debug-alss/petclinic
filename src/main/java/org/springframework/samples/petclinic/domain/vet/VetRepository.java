package org.springframework.samples.petclinic.domain.vet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.List;
import java.util.Optional;

public interface VetRepository extends JpaRepository<Vet, Integer> {
	List<Vet> findAllByOrderById();

	Optional<Vet> findById(int id);
}
