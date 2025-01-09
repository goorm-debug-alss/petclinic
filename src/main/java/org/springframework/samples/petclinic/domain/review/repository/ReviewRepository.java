package org.springframework.samples.petclinic.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.domain.review.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	@Query("SELECT r FROM Review r WHERE r.vet.id = :vetId")
	Optional<List<Review>> findByVetId(@Param("vetId") Integer vetId);

	@Query("SELECT r FROM Review r WHERE r.owner.id = :ownerId")
	Optional<List<Review>> findByOwnerId(@Param("ownerId") Integer ownerId);
}
