package org.springframework.samples.petclinic.domain.visit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.visit.model.Visit;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit,Integer> {

	List<Visit> findAllByPet(Pet pet);
}
