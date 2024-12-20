package domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.springframework.samples.petclinic.domain.review.mapper.ReviewHelper;
import org.springframework.samples.petclinic.domain.review.model.Review;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.review.service.ReviewReadService;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * ReviewReadService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class ReviewReadServiceTest {

	@InjectMocks
	private ReviewReadService reviewReadService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReviewHelper reviewHelper;

	private Owner owner;
	private Vet vet;
	private Review review;

	@BeforeEach
	void setUp() {
		createTestOwner();
		createTestVet();
		createTestReview();
	}

	@Test
	@DisplayName("특정 소유자의 리뷰 목록 조회")
	void getReviewsByOwner() {
		// given
		when(reviewRepository.findByOwnerId(owner.getId())).thenReturn(List.of(review));

		// when
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByOwner(owner);

		// then
		assertThat(reviews).isNotEmpty();
		assertThat(reviews).hasSize(1);
		assertThat(reviews.get(0).getBody().getContent()).isEqualTo("굳");

		verify(reviewRepository, times(1)).findByOwnerId(owner.getId());
	}

	@Test
	@DisplayName("특정 수의사의 리뷰 목록 조회")
	void getReviewsByVet() {
		// given
		when(reviewRepository.findByVetId(vet.getId())).thenReturn(List.of(review));

		// when
		List<ReviewResponseDto> reviews = reviewReadService.getReviewsByVet(vet);

		// then
		assertThat(reviews).isNotEmpty();
		assertThat(reviews).hasSize(1);
		assertThat(reviews.get(0).getBody().getVetId()).isEqualTo(vet.getId());

		verify(reviewRepository, times(1)).findByVetId(vet.getId());
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

	private void createTestReview() {
		review = Review.builder()
			.id(1)
			.content("굳")
			.vetId(vet)
			.ownerId(owner)
			.build();
	}
}
