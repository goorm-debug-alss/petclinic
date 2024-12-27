package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewEntityRetrievalService;
import org.springframework.samples.petclinic.domain.review.service.ReviewReadService;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * ReviewReadService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class ReviewReadServiceTest {

	@InjectMocks
	private ReviewReadService reviewReadService;

	@Mock
	private ReviewEntityRetrievalService retrievalService;

	@Mock
	private ReviewRepository reviewRepository;


	private Owner mockOwner;
	private Vet mockVet;
	private Review mockReview;

	@BeforeEach
	void setUp() {
		mockOwner = createTestOwner();
		mockVet = createTestVet();
		mockReview = createTestReview();
	}

	@Test
	@DisplayName("특정 소유자의 리뷰 목록 조회")
	void getReviewsByOwner() {
		// given
		when(retrievalService.fetchOwnerByIdOrThrow(mockOwner.getId())).thenReturn(mockOwner);
		when(reviewRepository.findByOwnerId(mockOwner.getId())).thenReturn(List.of(mockReview));

		// when
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByOwner(mockOwner.getId());

		// then
		assertThat(reviews).isNotEmpty();
		assertThat(reviews).hasSize(1);
		assertThat(reviews.get(0).getContent()).isEqualTo("굳");

		verify(reviewRepository, times(1)).findByOwnerId(mockOwner.getId());
	}

	@Test
	@DisplayName("특정 수의사의 리뷰 목록 조회")
	void getReviewsByVet() {
		// given
		when(retrievalService.fetchVetByIdOrThrow(mockVet.getId())).thenReturn(mockVet);
		when(reviewRepository.findByVetId(mockVet.getId())).thenReturn(List.of(mockReview));

		// when
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByVet(mockVet.getId());

		// then
		assertThat(reviews).isNotEmpty();
		assertThat(reviews).hasSize(1);
		assertThat(reviews.get(0).getVetId()).isEqualTo(mockVet.getId());

		verify(reviewRepository, times(1)).findByVetId(mockVet.getId());
	}

	@Test
	@DisplayName("특정 수의사의 리뷰 목록 조회 - 실패")
	void getReviewsByVet_NotFound() {
		// given
		when(retrievalService.fetchVetByIdOrThrow(mockVet.getId())).thenReturn(mockVet);
		when(reviewRepository.findByVetId(mockVet.getId())).thenReturn(List.of());

		// when
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByVet(mockVet.getId());

		// then
		assertThat(reviews).isEmpty();
		verify(reviewRepository, times(1)).findByVetId(mockVet.getId());
	}

	private Owner createTestOwner() {
		return Owner.builder()
			.id(1)
			.name("구름")
			.build();
	}

	private Vet createTestVet() {
		return Vet.builder()
			.id(1)
			.name("수의사")
			.build();
	}

	private Review createTestReview() {
		return Review.builder()
			.id(1)
			.content("굳")
			.vetId(mockVet)
			.ownerId(mockOwner)
			.build();
	}
}
