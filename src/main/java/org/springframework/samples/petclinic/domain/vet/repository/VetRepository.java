package org.springframework.samples.petclinic.domain.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.enums.VetStatus;

import java.util.List;
import java.util.Optional;

public interface VetRepository extends JpaRepository<Vet, Integer> {
	List<Vet> findAllByStatusOrderById(VetStatus status);

	Optional<Vet> findByIdAndStatus(Integer id, VetStatus status);
}
