package org.springframework.samples.petclinic.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	@Query("SELECT r FROM Review r WHERE r.vetId.id = :vetId")
	List<Review> findByVetId(@Param("vetId") Integer vetId);

	@Query("SELECT r FROM Review r WHERE r.ownerId.id = :ownerId")
	List<Review> findByOwnerId(@Param("ownerId") Integer ownerId);

	Object findByOwnerIdAndVetId(Owner ownerId, Vet vetId);

}
