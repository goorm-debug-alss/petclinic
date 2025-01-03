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
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewMapper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.UpdateReviewService;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateReviewServiceTest {

	@InjectMocks
	private UpdateReviewService updateReviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private VetRepository vetRepository;

	@Mock
	private ReviewMapper reviewMapper;

	private Owner mockOwner;
	private Vet mockVet;
	private Review mockReview;
	private ReviewRequestDto request;
	private ReviewResponseDto mockResponse;

	@BeforeEach
	void setUp() {
		createMockOwnerTestData();
		createMockVetTestData();
		createMockReviewTestData();
		createMockRequestTestData();
		createMockResponseTestData();
	}

	@Test
	@DisplayName("리뷰 업데이트 성공 - 유효한 요청 데이터와 ID가 제공되었을 때, 리뷰가 성공적으로 업데이트된다")
	void validRequestAndId_updateReview_updatesSuccessfully() {
		// given
		when(ownerRepository.findById(1)).thenReturn(Optional.of(mockOwner));
		when(reviewRepository.findById(1)).thenReturn(Optional.of(mockReview));
		when(vetRepository.findById(1)).thenReturn(Optional.of(mockVet));
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(reviewMapper.toDto(any(Review.class))).thenReturn(mockResponse);

		// when
		ReviewResponseDto response = updateReviewService.updateReview(request, 1, 1);

		// then
		assertThat(response.getContent()).isEqualTo("Test Updated Review");
	}

	@Test
	@DisplayName("소유자 조회 실패 - 유효하지 않은 주인 ID가 제공되었을 때, 예외가 발생한다")
	void invalidOwnerId_updateReview_throwApiException() {
		// given
		when(reviewRepository.findById(1)).thenReturn(Optional.of(mockReview));
		when(ownerRepository.findById(1)).thenThrow(new ApiException(OwnerErrorCode.NO_OWNER));

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, 1, 1));
	}

	@Test
	@DisplayName("리뷰 조회 실패 - 유효하지 않은 리뷰 ID가 제공되었을 때, 예외가 발생한다")
	void invalidReviewId_updateReview_throwApiException() {
		// given
		when(reviewRepository.findById(1)).thenThrow(new ApiException(ReviewErrorCode.NO_REVIEW));

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, 1, 1));
	}

	@Test
	@DisplayName("리뷰 접근 실패 - 소유자가 아닌 경우 예외가 발생한다")
	void unauthorizedOwner_updateReview_throwApiException() {
		// given
		Owner anotherOwner = Owner.builder()
			.id(2)
			.build();

		when(ownerRepository.findById(2)).thenReturn(Optional.of(anotherOwner));
		when(reviewRepository.findById(1)).thenReturn(Optional.of(mockReview));

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, 2, 1));
	}

	@Test
	@DisplayName("수의사 조회 실패 - 유효하지 않은 수의사 ID가 제공되었을 때, 예외가 발생한다")
	void invalidVetId_updateReview_throwApiException() {
		// given
		when(reviewRepository.findById(1)).thenReturn(Optional.of(mockReview));
		when(ownerRepository.findById(1)).thenReturn(Optional.of(mockOwner));
		when(vetRepository.findById(1)).thenThrow(new ApiException(VetErrorCode.NO_VET));

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, 1, 1));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - 점수가 null인 경우 예외가 발생한다")
	void nullScore_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(null)
			.content("Test Review")
			.build();

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, mockOwner.getId(), mockReview.getId()));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - 점수가 1 미만인 경우 예외가 발생한다")
	void zeroScore_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(0)
			.content("Test Review")
			.build();

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, mockOwner.getId(), mockReview.getId()));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - 점수가 5를 초과하는 경우 예외가 발생한다")
	void scoreAboveMax_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(10)
			.content("Test Review")
			.build();

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, mockOwner.getId(), mockReview.getId()));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - 내용이 null인 경우 예외가 발생한다")
	void nullContent_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(5)
			.content(null)
			.build();

		// when & then
		assertThrows(ApiException.class, () -> updateReviewService.updateReview(request, mockOwner.getId(), mockReview.getId()));
	}

	private void createMockOwnerTestData() {
		mockOwner = Owner.builder()
			.id(1)
			.build();
	}

	private void createMockVetTestData() {
		mockVet = Vet.builder()
			.id(1)
			.build();
	}

	private void createMockReviewTestData() {
		mockReview = Review.builder()
			.id(1)
			.ownerId(mockOwner)
			.vetId(mockVet)
			.score(1)
			.content("Test Initial Review")
			.build();
	}

	private void createMockRequestTestData() {
		request = ReviewRequestDto.builder()
			.score(5)
			.content("Test Updated Review")
			.build();
	}

	private void createMockResponseTestData() {
		mockResponse = ReviewResponseDto.builder()
			.score(request.getScore())
			.content(request.getContent())
			.build();
	}
}
