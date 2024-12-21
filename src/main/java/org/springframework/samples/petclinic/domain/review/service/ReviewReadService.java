package org.springframework.samples.petclinic.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 리뷰 조회 서비스
 * - 리뷰와 관련된 읽기 작업을 처리
 */
@Service
@RequiredArgsConstructor
public class ReviewReadService {

	private final ReviewRepository reviewRepository;
	private final OwnerRepository ownerRepository;
	private final VetRepository vetRepository;

	/**
	 * 특정 소유자가 작성한 리뷰 목록 조회
	 *
	 * @param ownerId 리뷰를 작성한 소유자
	 * @return 소유자가 작성한 리뷰의 리스트
	 */
	public List<ReviewResponseDto> getReviewsByOwner(Integer ownerId) {
		return getReviews(ownerId, this::fetchOwnerByIdOrThrow, id ->
				Optional.ofNullable(reviewRepository.findByOwnerId(id))
						.orElse(Collections.emptyList()));
	}

	/**
	 * 특정 수의사에 대한 리뷰 목록을 조회
	 *
	 * @param vetId 리뷰 대상 수의사
	 * @return 수의사에 대한 리뷰의 리스트
	 */
	public List<ReviewResponseDto> getReviewsByVet(Integer vetId) {
		return getReviews(vetId, this::fetchVetByIdOrThrow, id ->
				Optional.ofNullable(reviewRepository.findByVetId(id))
						.orElse(Collections.emptyList()));
	}

	private <T>	List<ReviewResponseDto> getReviews(Integer id, Function<Integer, T> fetcher, Function<Integer, List<Review>> finder) {
		fetcher.apply(id);
		return finder.apply(id).stream()
				.map(ReviewHelper::buildResponseDto)
				.collect(Collectors.toList());
	}

	private Owner fetchOwnerByIdOrThrow(Integer ownerId) {
		return ownerRepository.findById(ownerId).orElseThrow(() -> new OwnerNotFoundException("Invalid owner ID"));
	}

	private Vet fetchVetByIdOrThrow(Integer vetId) {
		return vetRepository.findById(vetId).orElseThrow(() -> new VetNotFoundException("Invalid vet ID"));
	}
}
