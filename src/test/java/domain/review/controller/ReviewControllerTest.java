package domain.review.controller;

import org.junit.jupiter.api.DisplayName;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.review.dto.ReviewRequestDto;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.domain.review.dto.ReviewResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.token.helper.JwtTokenHelper;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = PetClinicApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private VetRepository vetRepository;

	private String token;
	private String token1;

	@BeforeEach
	void setUp() {
		reviewRepository.deleteAll();
		token = generateTestToken();
		token1 = generateTestToken1();
	}

	private String generateTestToken() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("ownerId", 1);
		claims.put("role", "ROLE_USER");
		return jwtTokenHelper.issueAccessToken(claims).getToken();
	}

	private String generateTestToken1() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("ownerId", 2);
		claims.put("role", "ROLE_USER");
		return jwtTokenHelper.issueAccessToken(claims).getToken();
	}

	private static ReviewRequestDto createReviewRequestDto(int score, String content, Integer vetId) {
		return ReviewRequestDto.builder()
			.score(score)
			.content(content)
			.vetId(vetId)
			.build();
	}

	@Test
	@DisplayName("GET /review - 전체 리뷰 조회 성공")
	void getAllReviews_shouldReturnAllReviews() throws Exception {
		// given: 두 개의 리뷰 데이터와 수의사를 생성
		Vet vet1 = Vet.builder().name("수의사1").build();
		Vet vet2 = Vet.builder().name("수의사2").build();
		vetRepository.save(vet1);
		vetRepository.save(vet2);
		ReviewRequestDto request1 = createReviewRequestDto(5, "이 분 믿을만합니다 아주 실력이 좋아요", vet1.getId());
		ReviewRequestDto request2 = createReviewRequestDto(4, "베리 굳입니다 굳굳 재방문의사있음", vet2.getId());

		mockMvc.perform(post("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request1)))
			.andReturn().getResponse();

		mockMvc.perform(post("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request2)))
			.andReturn().getResponse();

		// when: 전체 리뷰 조회 API 호출
		MockHttpServletResponse response = mockMvc.perform(get("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 리뷰 데이터가 반환되어야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		ReviewResponseDto[] reviewResponseDtos = objectMapper.readValue(response.getContentAsString(), ReviewResponseDto[].class);
		assertThat(reviewResponseDtos).hasSize(2);
		assertThat(reviewResponseDtos[0].getContent()).isEqualTo("이 분 믿을만합니다 아주 실력이 좋아요");
		assertThat(reviewResponseDtos[1].getContent()).isEqualTo("베리 굳입니다 굳굳 재방문의사있음");
	}

	@Test
	@DisplayName("POST /review - 리뷰 생성 시 유요한 데이터를 제공하면 리뷰가 성공적으로 생성된다")
	void createReview_shouldCreateReviewWhenValidDataProvided() throws Exception {
		// given: 유효힌 리뷰 데이터 생성
		ReviewRequestDto request = createReviewRequestDto(5, "Test Review", 1);

		// when: 리뷰 생성 API 호출
		MockHttpServletResponse response = mockMvc.perform(post("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 생성된 리뷰 ID가 유효한 값이어야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		ReviewResponseDto reviewResponseDto = objectMapper.readValue(response.getContentAsString(), ReviewResponseDto.class);
		assertThat(reviewResponseDto.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("GET /review - 사용자 리뷰 조회 시 생성된 사용자 리뷰 데이터가 반환된다")
	void readMyReviews_shouldReturnUserReviews() throws Exception {
		// given: 두 개의 리뷰 데이터를 생성
		ReviewRequestDto request = createReviewRequestDto(5, "Test Review", 1);
		ReviewRequestDto request1 = createReviewRequestDto(5, "Test Review", 2);

		mockMvc.perform(post("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn().getResponse();

		mockMvc.perform(post("/review")
				.header("Authorization", token1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request1)))
			.andReturn().getResponse();

		// when: 사용자 리뷰 조회 API 호출
		MockHttpServletResponse response = mockMvc.perform(get("/review/my")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 사용자의 예약 데이터가 반횐되어야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		ReviewResponseDto[] reviewResponseDto = objectMapper.readValue(response.getContentAsString(), ReviewResponseDto[].class);
		assertThat(reviewResponseDto[0].getOwnerId()).isEqualTo(1);
		assertThat(reviewResponseDto[0].getContent()).isEqualTo("Test Review");
	}

	@Test
	@DisplayName("GET /review/{vetId} - 특정 수의사 리뷰 조회 시 해당 리뷰 데이터가 반환된다")
	void readVetReviews_shouldReturnReviewsForSpecificVet() throws Exception {
		// given: 하나의 리뷰 데이터를 생성
		ReviewRequestDto request = createReviewRequestDto(5, "Test Review", 1);

		MockHttpServletResponse createResponse = mockMvc.perform(post("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn().getResponse();

		ReviewResponseDto createReview = objectMapper.readValue(createResponse.getContentAsString(), ReviewResponseDto.class);
		Integer vetId = createReview.getVetId();

		// when: 수의사 리뷰 조회 API 호출
		MockHttpServletResponse response = mockMvc.perform(get("/review/{vetId}", vetId)
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 반환된 데이터 수의사 ID가 일치해야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		ReviewResponseDto[] reviewResponseDto = objectMapper.readValue(response.getContentAsString(), ReviewResponseDto[].class);
		assertThat(reviewResponseDto[0].getVetId()).isEqualTo(vetId);
	}

	@Test
	@DisplayName("PUT /review/{reviewId} - 리뷰 데이터를 수정하면 업데이트된 데이터가 반환된다")
	void updateReview_shouldUpdateReviewWhenValidDataProvided() throws Exception {
		// given: 하나의 리뷰 데이터를 생성 후 업데이트 할 데이터 준비
		ReviewRequestDto initialRequest = createReviewRequestDto(5, "Test Initial Review", 1);
		MockHttpServletResponse createResponse = mockMvc.perform(post("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(initialRequest)))
			.andReturn().getResponse();

		AppointmentResponseDto createReview = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer reviewId = createReview.getId();

		ReviewRequestDto updateRequest = createReviewRequestDto(5, "Test Updated Review", 1);

		// when: 리뷰 수정 API 호출
		MockHttpServletResponse response = mockMvc.perform(put("/review/{reviewId}", reviewId)
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 반환된 데이터가 업데이트된 데이터와 일치해야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		ReviewResponseDto reviewResponseDto = objectMapper.readValue(response.getContentAsString(), ReviewResponseDto.class);
		assertThat(reviewResponseDto.getContent()).isEqualTo("Test Updated Review");
	}

	@Test
	@DisplayName("DELETE /review/{reviewId} - 리뷰를 삭제하면 성공적으로 처리된다")
	void deleteReview_shouldDeleteReviewWhenValidIdProvided() throws Exception {
		// given: 하나의 리뷰 데이터를 생성
		ReviewRequestDto request = createReviewRequestDto(5, "Test Review", 1);

		MockHttpServletResponse createResponse = mockMvc.perform(post("/review")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn().getResponse();

		AppointmentResponseDto createReview = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer reviewId = createReview.getId();

		// when: 리뷰 삭제 API 호출
		MockHttpServletResponse deleteResponse = mockMvc.perform(delete("/review/{reviewId}", reviewId)
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이어야 한다
		assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
	}
}
