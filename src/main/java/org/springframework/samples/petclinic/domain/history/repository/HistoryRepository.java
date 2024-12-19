package org.springframework.samples.petclinic.domain.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.visit.model.Visit;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {

	List<History> findAllByVisitId_PetId(Pet pet);
}
