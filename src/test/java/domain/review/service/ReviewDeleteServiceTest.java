package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.exception.ReviewIdNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.ReviewNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.ReviewOwnershipException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewDeleteService;
import org.springframework.samples.petclinic.domain.review.service.ReviewEntityRetrievalService;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

/**
 * ReviewDeleteService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class ReviewDeleteServiceTest {

	@InjectMocks
	private ReviewDeleteService reviewDeleteService;

	@Mock
	private ReviewEntityRetrievalService retrievalService;

	@Mock
	private ReviewRepository reviewRepository;

	private Owner mockOwner;
	private Review mockReview;

	@BeforeEach
	void setUp() {
		mockOwner = createTestOwner();
		mockReview = createTestReview();
	}

	@Test
	@DisplayName("리뷰 삭제 성공")
	void deleteReview_Success() {
		// given
		when(retrievalService.fetchReviewByIdOrThrow(mockReview.getId())).thenReturn(mockReview);

		// when
		reviewDeleteService.deleteReview(1, mockOwner);

		// then
		verify(retrievalService).fetchReviewByIdOrThrow(mockReview.getId());
		verify(reviewRepository).delete(mockReview);
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - Review ID가 존재하지 않을 때")
	void deleteReview_ReviewNotFound() {
		// given
		when(retrievalService.fetchReviewByIdOrThrow(mockReview.getId()))
			.thenThrow(new ReviewIdNotFoundException(mockReview.getId()));

		// when & then
		assertThrows(ReviewIdNotFoundException.class, () ->
			reviewDeleteService.deleteReview(mockReview.getId(), mockOwner));

		verify(retrievalService).fetchReviewByIdOrThrow(mockReview.getId());
		verify(reviewRepository, never()).delete(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - 본인이 작성한 리뷰가 아닐 때")
	void deleteReview_ReviewOwnershipException() {
		// given
		Owner anotherOwner = Owner.builder()
			.id(2)
			.name("다른 사용자")
			.build();

		when(retrievalService.fetchReviewByIdOrThrow(mockReview.getId())).thenReturn(mockReview);

		// when & then
		assertThrows(ReviewOwnershipException.class, () ->
			reviewDeleteService.deleteReview(mockReview.getId(), anotherOwner));

		verify(retrievalService).fetchReviewByIdOrThrow(mockReview.getId());
		verify(reviewRepository, never()).delete(any());
	}

	private Owner createTestOwner() {
		return Owner.builder()
			.id(1)
			.name("구름")
			.build();
	}

	private Review createTestReview() {
		return Review.builder()
			.id(1)
			.content("굳")
			.ownerId(mockOwner)
			.build();
	}
}
