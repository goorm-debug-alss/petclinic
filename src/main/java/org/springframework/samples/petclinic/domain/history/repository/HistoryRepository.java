package org.springframework.samples.petclinic.domain.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.history.model.History;

public interface HistoryRepository extends JpaRepository<History, Integer> {
}
