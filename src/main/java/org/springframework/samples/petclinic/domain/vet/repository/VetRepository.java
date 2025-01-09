package org.springframework.samples.petclinic.domain.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.List;

public interface VetRepository extends JpaRepository<Vet, Integer> {
	List<Vet> findAllByOrderById();
}
