package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.error.ReviewErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.DeleteReviewService;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DeleteReviewServiceTest {

	@InjectMocks
	private DeleteReviewService deleteReviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private OwnerRepository ownerRepository;

	private Review mockReview;
	private Owner mockOwner;

	@BeforeEach
	void setUp() {
		createMockOwnerTestData();
		createMockReviewTestData();

	}

	@Test
	@DisplayName("리뷰 삭제 성공 - 유효한 리뷰 ID와 소유자 ID를 제공하면 리뷰를 성공적으로 삭제한다")
	void validReviewIdAndOwnerId_deleteReview_deletesSuccessfully() {
		// given
		when(ownerRepository.findById(1)).thenReturn(Optional.of(mockOwner));
		when(reviewRepository.findById(1)).thenReturn(Optional.of(mockReview));

		// when
		deleteReviewService.deleteReview(1, 1);

		// then
		verify(ownerRepository, times(1)).findById(1);
		verify(reviewRepository, times(1)).findById(1);
		verify(reviewRepository, times(1)).delete(mockReview);
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - 소유자를 찾을 수 없을 때, 예외가 발생한다")
	void noOwnerFound_deleteReview_throwsApiException() {
		// given
		when(ownerRepository.findById(1)).thenThrow(new ApiException(OwnerErrorCode.NO_OWNER));

		// when & then
		assertThrows(ApiException.class, () -> deleteReviewService.deleteReview(1, 1));
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - 리뷰를 찾을 없을 때, 예외가 발생한다")
	void noReviewFound_deleteReview_throwsApiException() {
		// given
		when(ownerRepository.findById(1)).thenReturn(Optional.of(mockOwner));
		when(reviewRepository.findById(1)).thenThrow(new ApiException(ReviewErrorCode.NO_REVIEW));

		// when & then
		assertThrows(ApiException.class, () -> deleteReviewService.deleteReview(1, 1));
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - 리뷰 소유자가 아닌 경우 예외가 발생한다")
	void notReviewOwner_deleteReview_throwsApiException() {
		// given
		Owner anotherOwner = Owner.builder()
			.id(2)
			.build();

		when(ownerRepository.findById(2)).thenReturn(Optional.of(anotherOwner));
		when(reviewRepository.findById(1)).thenReturn(Optional.of(mockReview));

		// when & then
		assertThrows(ApiException.class, () -> deleteReviewService.deleteReview(1, 2));
	}

	private void createMockOwnerTestData() {
		mockOwner = Owner.builder()
			.id(1)
			.build();
	}

	private void createMockReviewTestData() {
		mockReview = Review.builder()
			.id(1)
			.owner(mockOwner)
			.build();
	}
}
