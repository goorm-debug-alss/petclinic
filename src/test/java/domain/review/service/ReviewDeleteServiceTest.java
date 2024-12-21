package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.exception.ReviewNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.ReviewOwnershipException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewDeleteService;

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
	private ReviewRepository reviewRepository;

	private Owner owner;
	private Review review;

	@BeforeEach
	void setUp() {
		createTestOwner();
		createTestReview();
	}

	@Test
	@DisplayName("리뷰 삭제 성공")
	void deleteReview_Success() {
		// given
		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

		// when
		reviewDeleteService.deleteReview(1, owner);

		// then
		verify(reviewRepository).findById(review.getId());
		verify(reviewRepository).delete(review);
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - Review ID가 존재하지 않을 때")
	void deleteReview_ReviewNotFound() {
		// given
		when(reviewRepository.findById(review.getId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(ReviewNotFoundException.class, () ->
			reviewDeleteService.deleteReview(1, owner));

		verify(reviewRepository).findById(review.getId());
		verify(reviewRepository, never()).delete(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - 본인이 작성한 리뷰가 아닐 때")
	void deleteReview_ReviewOwnershipException() {
		// given
		Owner anotherOwner = Owner.builder()
			.id(2)
			.name("구르미")
			.build();

		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

		// when & then
		assertThrows(ReviewOwnershipException.class, () ->
			reviewDeleteService.deleteReview(1, anotherOwner));

		verify(reviewRepository).findById(review.getId());
		verify(reviewRepository, never()).delete(any(Review.class));
	}

	private void createTestOwner() {
		owner = Owner.builder()
			.id(1)
			.name("구름")
			.build();
	}

	private void createTestReview() {
		review = Review.builder()
			.id(1)
			.content("굳")
			.ownerId(owner)
			.build();
	}
}
