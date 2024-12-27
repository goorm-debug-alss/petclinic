package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.exception.*;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewEntityRetrievalService;
import org.springframework.samples.petclinic.domain.review.service.ReviewUpdateService;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReviewUpdateService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class ReviewUpdateServiceTest {

	@InjectMocks
	private ReviewUpdateService reviewUpdateService;

	@Mock
	private ReviewEntityRetrievalService retrievalService;

	@Mock
	private ReviewRepository reviewRepository;

	private Owner mockOwner;
	private Vet mockVet;
	private Review mockReview;
	private ReviewRequestDto requestDto;

	@BeforeEach
	void setUp() {
		mockOwner = createTestOwner();
		mockVet = createTestVet();
		mockReview = createTestReview();
		requestDto = createTestUpdateReview();
	}

	@Test
	@DisplayName("리뷰 업데이트 성공")
	void updateReview_Success() {
		// given
		when(retrievalService.fetchReviewByIdOrThrow(mockReview.getId())).thenReturn(mockReview);
		when(retrievalService.fetchVetByIdOrThrow(requestDto.getVetId())).thenReturn(mockVet);
		when(reviewRepository.save(any(Review.class))).thenReturn(mockReview);

		// when
		ReviewResponseDto response = reviewUpdateService.updateReview(mockReview.getId(), requestDto, mockOwner);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).isEqualTo("업데이트 굳");
		verify(retrievalService).fetchReviewByIdOrThrow(mockReview.getId());
		verify(retrievalService).fetchVetByIdOrThrow(requestDto.getVetId());
		verify(reviewRepository).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - Review ID가 존재하지 않을 때")
	void updateReview_ReviewNotFound() {
		// given
		when(retrievalService.fetchReviewByIdOrThrow(mockReview.getId())).thenThrow(new ReviewIdNotFoundException(mockReview.getId()));

		// when & then
		assertThrows(ReviewIdNotFoundException.class, () ->
			reviewUpdateService.updateReview(mockReview.getId(), requestDto, mockOwner));

		verify(retrievalService).fetchReviewByIdOrThrow(mockReview.getId());
		verify(retrievalService, never()).fetchVetByIdOrThrow(anyInt());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - Owner ID가 일치하지 않을 때")
	void updateReview_InvalidOwner() {
		// given
		Owner invalidOwner = Owner.builder().id(2).name("구르미").build();
		Review mockReview = mock(Review.class);
		when(mockReview.getOwnerId()).thenReturn(mockOwner);
		when(retrievalService.fetchReviewByIdOrThrow(mockReview.getId())).thenReturn(mockReview);

		// when & then
		assertThrows(ReviewOwnershipException.class, () ->
			reviewUpdateService.updateReview(mockReview.getId(), requestDto, invalidOwner));

		// verify
		verify(retrievalService).fetchReviewByIdOrThrow(mockReview.getId());
		verify(retrievalService, never()).fetchVetByIdOrThrow(anyInt());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - Vet ID가 존재하지 않을 때")
	void updateReview_InvalidVet() {
		// given
		when(retrievalService.fetchReviewByIdOrThrow(mockReview.getId())).thenReturn(mockReview);
		when(retrievalService.fetchVetByIdOrThrow(requestDto.getVetId())).thenThrow(new VetNotFoundException(mockVet.getId()));

		// when & then
		assertThrows(VetNotFoundException.class, () ->
			reviewUpdateService.updateReview(mockReview.getId(), requestDto, mockOwner));

		verify(retrievalService).fetchReviewByIdOrThrow(mockReview.getId());
		verify(retrievalService).fetchVetByIdOrThrow(requestDto.getVetId());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	private Owner createTestOwner() {
		return Owner.builder().id(1).name("구름").build();
	}

	private Vet createTestVet() {
		return Vet.builder().id(1).name("수의사").build();
	}

	private Review createTestReview() {
		return Review.builder()
			.id(1)
			.ownerId(mockOwner)
			.vetId(mockVet)
			.content("굳")
			.build();
	}


	private ReviewRequestDto createTestUpdateReview() {
		return ReviewRequestDto.builder()
			.vetId(1)
			.content("업데이트 굳")
			.build();
	}
}
