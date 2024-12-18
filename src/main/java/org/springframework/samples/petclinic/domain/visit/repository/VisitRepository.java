package org.springframework.samples.petclinic.domain.visit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.visit.model.Visit;

public interface VisitRepository extends JpaRepository<Visit,Integer> {

}
