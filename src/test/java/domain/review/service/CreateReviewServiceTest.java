package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.OwnerErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewMapper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.CreateReviewService;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.service.VetService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateReviewServiceTest {

	@InjectMocks
	private CreateReviewService createReviewService;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private VetService vetService;

	@Mock
	private VetRepository vetRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReviewMapper reviewMapper;

	private Owner mockOwner;
	private Vet mockVet;
	private Review mockReview;
	private ReviewRequestDto request;

	@BeforeEach
	void setUp() {
		createMockOwnerTestData();
		createMockVetTestData();
		createMockRequestTestData();
		createMockReviewTestData();
	}

	@Test
	@DisplayName("리뷰 생성 성공 - 유효한 요청 데이터를 제공하면 리뷰가 성공적으로 생성된다")
	void validRequest_createReview_createSuccessfully() {
		// given
		when(ownerRepository.findById(1)).thenReturn(Optional.of(mockOwner));
		when(vetService.getVetOrThrow(1)).thenReturn(mockVet);
		when(reviewMapper.toEntity(request, mockOwner, mockVet)).thenReturn(mockReview);
		when(reviewRepository.save(any(Review.class))).thenReturn(mockReview);

		// when
		Review review = createReviewService.createReview(request, mockOwner.getId());

		// then
		assertThat(review.getScore()).isEqualTo(5);
		assertThat(review.getContent()).isEqualTo("Test Review");
		assertThat(review.getOwner()).isEqualTo(mockOwner);
		assertThat(review.getVet()).isEqualTo(mockVet);
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 유효하지 않은 주인 ID가 제공되었을 때, 예외가 발생한다")
	void invalidOwnerId_createReview_throwsException() {
		// given
		when(ownerRepository.findById(1)).thenThrow(new ApiException(OwnerErrorCode.NO_OWNER));

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 유효하지 않은 수의사 ID가 제공되었을 때, 예외가 발생한다")
	void invalidVetId_createReview_throwsException() {
		// given
		when(vetService.getVetOrThrow(1)).thenThrow(new ApiException(VetErrorCode.NO_VET));

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 점수가 null인 경우 예외가 발생한다")
	void nullScore_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(null)
			.content("Test Review")
			.vetId(1)
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 점수가 1 미만인 경우 예외가 발생한다")
	void zeroScore_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(0)
			.content("Test Review")
			.vetId(1)
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 점수가 5를 초과하는 경우 예외가 발생한다")
	void scoreAboveMax_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(10)
			.content("Test Review")
			.vetId(1)
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 내용이 null인 경우 예외가 발생한다")
	void nullContent_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(5)
			.content(null)
			.vetId(1)
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 내용이 비어 있는 경우 예외가 발생한다")
	void emptyContent_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(5)
			.content("")
			.vetId(1)
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 내용이 200자를 초과하는 경우 예외가 발생한다")
	void contentExceedsMaxLength_createReview_throwsException() {
		// given
		request = ReviewRequestDto.builder()
			.score(5)
			.content("test".repeat(201))
			.vetId(1)
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createReviewService.createReview(request, mockOwner.getId()));
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

	private void createMockRequestTestData() {
		request = ReviewRequestDto.builder()
			.score(5)
			.content("Test Review")
			.vetId(1)
			.build();
	}

	private void createMockReviewTestData() {
		mockReview = Review.builder()
			.score(request.getScore())
			.content(request.getContent())
			.owner(mockOwner)
			.vet(mockVet)
			.build();
	}
}
