package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.ReviewErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewMapper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewMapper reviewMapper;
	private final VetRepository vetRepository;

	public List<ReviewResponseDto> findAllReviews(){
		return reviewRepository.findAll().stream()
			.map(reviewMapper::toDto)
			.collect(Collectors.toList());
	}

	public List<ReviewResponseDto> findMyReviews(Integer ownerId) {
		return getReviewsByOwnerId(ownerId).stream()
			.map(reviewMapper::toDto)
			.collect(Collectors.toList());
	}

	public List<ReviewResponseDto> findVetReviews(Integer vetId) {
		validateVetExists(vetId);

		return getReviewsByVetId(vetId).stream()
			.map(reviewMapper::toDto)
			.collect(Collectors.toList());
	}

	private List<Review> getReviewsByOwnerId(Integer ownerId) {
		return reviewRepository.findByOwnerId(ownerId)
			.orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));
	}

	private List<Review> getReviewsByVetId(Integer vetId) {
		return reviewRepository.findByVetId(vetId)
			.orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));
	}

	private void validateVetExists(Integer vetId) {
		vetRepository.findById(vetId)
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));
	}
}
