package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.ReviewErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewMapper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReadReviewService;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadReviewServiceTest {

	@InjectMocks
	private ReadReviewService readReviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private VetRepository vetRepository;

	@Mock
	private ReviewMapper reviewMapper;


	private Review mockReview;
	private Owner mockOwner;
	private Vet mockVet;
	private ReviewResponseDto mockResponse;

	@BeforeEach
	void setUp() {
		createMockOwnerTestData();
		createMockVetTestData();
		createMockReviewTestData();
		createMockResponseTestData();
	}

	@Test
	@DisplayName("리뷰 조회 성공 - 전체 리뷰 반환")
	void findAllReviews_returnReviewList() {
		// given
		when(reviewRepository.findAll()).thenReturn(List.of(mockReview));
		when(reviewMapper.toDto(mockReview)).thenReturn(mockResponse);

		// when
		List<ReviewResponseDto> reviews = readReviewService.findAllReviews();

		// then
		assertThat(reviews).hasSize(1);
		assertThat(reviews.get(0).getOwnerId()).isEqualTo(1);
		assertThat(reviews.get(0).getContent()).isEqualTo("Test Review");
	}

	@Test
	@DisplayName("리뷰 조회 실패 - 리뷰가 없으면 빈 리스트 반환")
	void findAllReviews_returnEmptyList() {
		// given
		when(reviewRepository.findAll()).thenReturn(List.of());

		// when
		List<ReviewResponseDto> reviews = readReviewService.findAllReviews();

		// then
		assertThat(reviews).isEmpty();
	}

	@Test
	@DisplayName("리뷰 조회 성공 - 유효한 Owner ID로 리뷰를 조회하면 해당 리뷰 목록을 반환한다")
	void validOwnerId_findMyReviews_returnReviewList() {
		// given
		when(reviewRepository.findByOwnerId(1)).thenReturn(Optional.of(List.of(mockReview)));
		when(reviewMapper.toDto(mockReview)).thenReturn(mockResponse);

		// when
		List<ReviewResponseDto> reviews = readReviewService.findMyReviews(1);

		// then
		assertThat(reviews).hasSize(1);
		assertThat(reviews.get(0).getOwnerId()).isEqualTo(1);
		assertThat(reviews.get(0).getContent()).isEqualTo("Test Review");
	}

	@Test
	@DisplayName("리뷰 조회 실패 - Owner ID로 조회 시 리뷰가 없으면 예외가 발생한다")
	void invalidOwnerId_findMyReviews_throwApiException() {
		// given
		when(reviewRepository.findByOwnerId(1)).thenThrow(new ApiException(ReviewErrorCode.NO_REVIEW));

		// when & then
		assertThrows(ApiException.class, () -> readReviewService.findMyReviews(1));
	}

	@Test
	@DisplayName("수의사 리뷰 조회 성공 - 유효한 Vet ID로 리뷰를 조회하면 해당 리뷰 목록을 반환한다")
	void validVetId_findVetReviews_returnReviewList() {
		// given
		when(vetRepository.findById(1)).thenReturn(Optional.of(mockVet));
		when(reviewRepository.findByVetId(1)).thenReturn(Optional.of(List.of(mockReview)));
		when(reviewMapper.toDto(mockReview)).thenReturn(mockResponse);

		// when
		List<ReviewResponseDto> reviews = readReviewService.findVetReviews(1);

		// then
		assertThat(reviews).hasSize(1);
		assertThat(reviews.get(0).getVetId()).isEqualTo(1);
		assertThat(reviews.get(0).getContent()).isEqualTo("Test Review");
	}

	@Test
	@DisplayName("수의사 리뷰 조회 실패 - 존재하지 않는 Vet ID로 조회 시 예외가 발생한다")
	void invalidVetId_findVetReviews_throwApiException() {
		// given
		when(vetRepository.findById(1)).thenThrow(new ApiException(VetErrorCode.NO_VET));

		// when & then
		assertThrows(ApiException.class, () -> readReviewService.findVetReviews(1));
	}

	@Test
	@DisplayName("수의사 리뷰 조회 실패 - 유효한 Vet ID지만 리뷰가 없으면 예외가 발생한다")
	void validVetIdNoReviews_findVetReviews_throwApiException() {
		// given
		when(vetRepository.findById(1)).thenReturn(Optional.of(mockVet));
		when(reviewRepository.findByVetId(1)).thenThrow(new ApiException(ReviewErrorCode.NO_REVIEW));

		// when & then
		assertThrows(ApiException.class, () -> readReviewService.findVetReviews(1));
	}

	private void createMockOwnerTestData() {
		mockOwner = Owner.builder()
			.id(1)
			.name("Test")
			.build();
	}

	private void createMockVetTestData() {
		mockVet = Vet.builder()
			.id(1)
			.name("Test")
			.build();
	}

	private void createMockReviewTestData() {
		mockReview = Review.builder()
			.id(1)
			.vetId(mockVet)
			.ownerId(mockOwner)
			.content("Test Review")
			.build();
	}

	private void createMockResponseTestData() {
		mockResponse = ReviewResponseDto.builder()
			.id(mockReview.getId())
			.score(mockReview.getScore())
			.content(mockReview.getContent())
			.createAt(mockReview.getCreatedAt())
			.vetId(mockReview.getVetId().getId())
			.ownerId(mockReview.getOwnerId().getId())
			.build();
	}
}
