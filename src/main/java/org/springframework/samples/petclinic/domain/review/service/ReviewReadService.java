package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewReadService {

	private final ReviewEntityRetrievalService entityRetrievalService;
	private final ReviewRepository reviewRepository;

	// 사용자가 작성한 리뷰 조회
	public List<ReviewResponseDto> getReviewsByOwner(Integer ownerId) {
		return getReviews(ownerId, getFetchReviewByIdOrThrow(), reviewRepository::findByOwnerId);
	}

	// 수의사 리뷰 조회
	public List<ReviewResponseDto> getReviewsByVet(Integer vetId) {
		return getReviews(vetId, getFetchVetByIdOrThrow(), reviewRepository::findByVetId);
	}

	private <T>	List<ReviewResponseDto> getReviews(Integer id, Function<Integer, T> fetcher, Function<Integer, List<Review>> finder) {
		fetcher.apply(id);
		return Optional.ofNullable(finder.apply(id))
			.orElse(Collections.emptyList())
			.stream()
			.map(ReviewHelper::buildResponseDto)
			.collect(Collectors.toList());
	}

	private Function<Integer, Owner> getFetchReviewByIdOrThrow() {
		return entityRetrievalService::fetchOwnerByIdOrThrow;
	}

	private Function<Integer, Vet> getFetchVetByIdOrThrow() {
		return entityRetrievalService::fetchVetByIdOrThrow;
	}
}
