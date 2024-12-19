package org.springframework.samples.petclinic.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	// Owner의 리뷰 개수 조회
	@Query("SELECT COUNT(r) FROM Review r WHERE r.ownerId.id = :ownerId")
	int countReviewsByOwnerId(@Param("ownerId") Integer ownerId);

	// Vet의 리뷰 개수 조회
	@Query("SELECT COUNT(r) FROM Review r WHERE r.vetId.id = :vetId")
	int countReviewsByVetId(@Param("vetId") Integer vetId);

	// 특정 Owner의 모든 리뷰 조회
	List<Review> findByOwnerId_Id(Integer ownerId);

	// 특정 Vet의 모든 리뷰 조회
	List<Review> findByVetId_Id(Integer vetId);

}


