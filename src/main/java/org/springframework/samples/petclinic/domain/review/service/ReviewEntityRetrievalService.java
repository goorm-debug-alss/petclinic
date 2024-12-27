package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.ReviewIdNotFoundException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewEntityRetrievalService {

	private final ReviewRepository reviewRepository;
	private final OwnerRepository ownerRepository;
	private final VetRepository vetRepository;

	@Transactional(readOnly = true)
	public Review fetchReviewByIdOrThrow(Integer reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewIdNotFoundException(reviewId));
	}

	@Transactional(readOnly = true)
	public Owner fetchOwnerByIdOrThrow(Integer ownerId) {
		return ownerRepository.findById(ownerId)
			.orElseThrow(() -> new OwnerNotFoundException(ownerId));
	}

	@Transactional(readOnly = true)
	public Vet fetchVetByIdOrThrow(Integer vetId) {
		return vetRepository.findById(vetId)
			.orElseThrow(() -> new VetNotFoundException(vetId));
	}
}
