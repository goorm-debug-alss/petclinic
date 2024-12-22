package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.exception.InvalidContentException;
import org.springframework.samples.petclinic.domain.review.exception.InvalidScoreException;
import org.springframework.samples.petclinic.domain.review.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewCreateService;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.Optional;

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
	private ReviewRepository reviewRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private VetRepository vetRepository;

	private ReviewRequestDto requestDto;
	private Owner owner;
	private Vet vet;

	@BeforeEach
	void setUp() {
		createTestReviewRequestDto();
		createTestOwner();
		createTestVet();
	}

	@Test
	@DisplayName("리뷰 생성 성공")
	void createReview_Success() {
		// given
		when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
		when(vetRepository.findById(vet.getId())).thenReturn(Optional.of(vet));
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		ReviewResponseDto responseDto = reviewCreateService.createReview(requestDto, 1, 1);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getBody().getScore()).isEqualTo(requestDto.getScore());
		assertThat(responseDto.getBody().getContent()).isEqualTo(requestDto.getContent());
		assertThat(responseDto.getBody().getOwnerId()).isEqualTo(owner.getId());
		assertThat(responseDto.getBody().getVetId()).isEqualTo(vet.getId());

		verify(ownerRepository).findById(owner.getId());
		verify(vetRepository).findById(vet.getId());
		verify(reviewRepository).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Score가 null일 때")
	void createReview_ScoreIsNull() {
		// given
		requestDto.setScore(null);

		// when & then
		assertThrows(InvalidScoreException.class, () ->
			reviewCreateService.createReview(requestDto, owner.getId(), vet.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Content가 비어 있을 때")
	void createReview_ContentIsEmpty() {
		// given
		requestDto.setContent("");

		// when & then
		assertThrows(InvalidContentException.class, () ->
			reviewCreateService.createReview(requestDto, owner.getId(), vet.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Owner ID가 존재하지 않을 때")
	void createReview_OwnerNotFound() {
		// given
		when(ownerRepository.findById(owner.getId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(OwnerNotFoundException.class, () ->
			reviewCreateService.createReview(requestDto, 1, 1));

		verify(ownerRepository).findById(owner.getId());
		verify(vetRepository, never()).findById(vet.getId());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Owner ID와 요청 데이터가 일치하지 않을 때")
	void createReview_OwnerIdMismatch() {
		// when & then
		assertThrows(OwnerNotFoundException.class, () ->
			reviewCreateService.createReview(requestDto, 999, vet.getId()));
	}

	@Test
	@DisplayName("리뷰 생성 실패 - Vet ID가 존재하지 않을 때")
	void createReview_VetNotFound() {
		// given
		when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
		when(vetRepository.findById(vet.getId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(VetNotFoundException.class, () ->
			reviewCreateService.createReview(requestDto, 1, 1));

		verify(ownerRepository).findById(owner.getId());
		verify(vetRepository).findById(vet.getId());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	private void createTestReviewRequestDto() {
		requestDto = ReviewRequestDto.builder()
			.score(5)
			.content("굳")
			.build();
	}

	private void createTestOwner() {
		owner = Owner.builder()
			.id(1)
			.name("구름")
			.build();
	}

	private void createTestVet() {
		vet = Vet.builder()
			.id(1)
			.name("수의사")
			.build();
	}
}
