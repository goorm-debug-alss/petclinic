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
import org.springframework.samples.petclinic.domain.review.exception.ReviewNotFoundException;
import org.springframework.samples.petclinic.domain.review.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewUpdateService;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.Optional;

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
	private ReviewRepository reviewRepository;

	@Mock
	private VetRepository vetRepository;

	private Owner owner;
	private Vet vet;
	private Review review;
	private ReviewRequestDto requestDto;

	@BeforeEach
	void setUp() {
		createTestOwner();
		createTestVet();
		createTestReview();
		createTestUpdateReview();
	}

	@Test
	@DisplayName("리뷰 업데이트 성공")
	void updateReview_Success() {
		// given
		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
		when(vetRepository.findById(requestDto.getVetId())).thenReturn(Optional.of(vet));
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		// when
		ReviewResponseDto response = reviewUpdateService.updateReview(review.getId(), requestDto, owner);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getBody().getContent()).isEqualTo("업데이트 굳");
		verify(reviewRepository).findById(review.getId());
		verify(vetRepository).findById(requestDto.getVetId());
		verify(reviewRepository).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - Review ID가 존재하지 않을 때")
	void updateReview_ReviewNotFound() {
		// given
		when(reviewRepository.findById(review.getId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(ReviewNotFoundException.class, () ->
			reviewUpdateService.updateReview(review.getId(), requestDto, owner));

		verify(reviewRepository).findById(review.getId());
		verify(vetRepository, never()).findById(anyInt());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - Owner ID가 일치하지 않을 때")
	void updateReview_InvalidOwner() {
		// given
		Owner invalidOwner = Owner.builder().id(2).name("구르미").build();
		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

		// when & then
		assertThrows(SecurityException.class, () ->
			reviewUpdateService.updateReview(review.getId(), requestDto, invalidOwner));

		verify(reviewRepository).findById(review.getId());
		verify(vetRepository, never()).findById(anyInt());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - Vet ID가 존재하지 않을 때")
	void updateReview_InvalidVet() {
		// given
		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
		when(vetRepository.findById(requestDto.getVetId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(VetNotFoundException.class, () ->
			reviewUpdateService.updateReview(review.getId(), requestDto, owner));

		verify(reviewRepository).findById(review.getId());
		verify(vetRepository).findById(requestDto.getVetId());
		verify(reviewRepository, never()).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 업데이트 실패 - 리뷰내용(content)이 비어있을때")
	void updateReview_contentIsEmpty() {
		// given
		requestDto.setContent(""); // 내용이 빈 문자열로 설정
		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

		// when & then
		assertThrows(IllegalArgumentException.class, () ->
			reviewUpdateService.updateReview(review.getId(), requestDto, owner));

		// verify
		verify(reviewRepository).findById(review.getId());
		verify(vetRepository, never()).findById(anyInt()); // 호출되지 않음을 검증
		verify(reviewRepository, never()).save(any(Review.class));
	}



	private void createTestOwner() {
		owner = Owner.builder().id(1).name("구름").build();
	}

	private void createTestVet() {
		vet = Vet.builder().id(1).name("수의사").build();
	}

	private void createTestReview() {
		review = Review.builder()
			.id(1)
			.ownerId(owner)
			.vetId(vet)
			.content("굳")
			.build();
	}

	private void createTestUpdateReview() {
		requestDto = ReviewRequestDto.builder()
			.vetId(1)
			.content("업데이트 굳")
			.build();
	}
}
