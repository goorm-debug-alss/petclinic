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
import org.springframework.samples.petclinic.domain.review.exception.InvalidContentException;
import org.springframework.samples.petclinic.domain.review.exception.InvalidScoreException;
import org.springframework.samples.petclinic.domain.review.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewCreateService;
import org.springframework.samples.petclinic.domain.review.service.ReviewEntityRetrievalService;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReviewCreateService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class ReviewCreateServiceTest {

	@InjectMocks
	private ReviewCreateService reviewCreateService;

	@Mock
	private ReviewEntityRetrievalService retrievalService;

	@Mock
	private VetRepository vetRepository;

	@Mock
	private ReviewRepository reviewRepository;

	private ReviewRequestDto requestDto;
	private Owner mockOwner;
	private Vet mockVet;

	@BeforeEach
	void setUp() {
		requestDto = createTestReviewRequestDto();
		mockOwner = createTestOwner();
		mockVet = createTestVet();
	}

	@Test
	@DisplayName("리뷰 생성 성공")
	void createReview_Success() {
		// given
		when(retrievalService.fetchOwnerByIdOrThrow(mockOwner.getId())).thenReturn(mockOwner);
		when(retrievalService.fetchVetByIdOrThrow(mockVet.getId())).thenReturn(mockVet);
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		ReviewResponseDto responseDto = reviewCreateService.createReview(requestDto, mockOwner.getId(), mockVet.getId());

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getScore()).isEqualTo(requestDto.getScore());
		assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());
		assertThat(responseDto.getOwnerId()).isEqualTo(mockOwner.getId());
		assertThat(responseDto.getVetId()).isEqualTo(mockVet.getId());

		verify(retrievalService).fetchOwnerByIdOrThrow(mockOwner.getId());
		verify(retrievalService).fetchVetByIdOrThrow(mockVet.getId());
		verify(reviewRepository).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Score가 null일 때")
	void createReview_ScoreIsNull() {
		// given
		requestDto.setScore(null);

		// when & then
		assertThrows(InvalidScoreException.class, () ->
			reviewCreateService.createReview(requestDto, mockOwner.getId(), mockVet.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Content가 비어 있을 때")
	void createReview_ContentIsEmpty() {
		// given
		requestDto.setContent("");

		// when & then
		assertThrows(InvalidContentException.class, () ->
			reviewCreateService.createReview(requestDto, mockOwner.getId(), mockVet.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Owner ID가 존재하지 않을 때")
	void createReview_OwnerNotFound() {
		// given
		when(retrievalService.fetchOwnerByIdOrThrow(mockOwner.getId())).thenThrow(new OwnerNotFoundException(mockOwner.getId()));

		// when & then
		assertThrows(OwnerNotFoundException.class, () ->
			reviewCreateService.createReview(requestDto, mockOwner.getId(), mockVet.getId()));

		verify(retrievalService).fetchOwnerByIdOrThrow(mockOwner.getId());
		verify(retrievalService, never()).fetchVetByIdOrThrow(mockVet.getId());
		verify(reviewRepository, never()).save(any());
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Vet ID가 존재하지 않을 때")
	void createReview_VetNotFound() {
		// given
		when(retrievalService.fetchOwnerByIdOrThrow(mockOwner.getId())).thenReturn(mockOwner);
		when(retrievalService.fetchVetByIdOrThrow(mockVet.getId())).thenThrow(new VetNotFoundException(mockVet.getId()));

		// when & then
		assertThrows(VetNotFoundException.class, () ->
			reviewCreateService.createReview(requestDto, mockOwner.getId(), mockVet.getId()));

		verify(retrievalService).fetchOwnerByIdOrThrow(mockOwner.getId());
		verify(retrievalService).fetchVetByIdOrThrow(mockVet.getId());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	private ReviewRequestDto createTestReviewRequestDto() {
		return ReviewRequestDto.builder()
			.score(5)
			.content("좋은 서비스")
			.build();
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
}
