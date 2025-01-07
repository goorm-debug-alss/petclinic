package domain.owner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.owner.dto.*;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.review.repository.ReviewRepository;
import org.springframework.samples.petclinic.domain.token.dto.TokenResponseDto;
import org.springframework.samples.petclinic.domain.token.helper.JwtTokenHelper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = PetClinicApplication.class)
@AutoConfigureMockMvc
@Transactional
public class OwnerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	private String token;

	@BeforeEach
	void setUp() {
		// given: 데이터 초기화
		reviewRepository.deleteAll();
		appointmentRepository.deleteAll();
		petRepository.deleteAll();
		ownerRepository.deleteAll();
		token = generateTestToken();
	}

	@Test
	@DisplayName("POST /owner/register - 소유자 등록이 성공적으로 처리된다")
	void registerOwner_shouldSucceed() throws Exception {
		// given
		RegisterRequestDto request = createRegisterRequestDto("TestUser", "TestName");

		// when
		MockHttpServletResponse response = performRequest("/owner/register", request, HttpMethod.POST);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		OwnerResponseDto responseDto = objectMapper.readValue(response.getContentAsString(), OwnerResponseDto.class);
		assertThat(responseDto.getName()).isEqualTo("TestName");
	}

	@Test
	@DisplayName("POST /owner/login - 로그인 요청이 성공적으로 처리된다")
	void loginOwner_shouldSucceed() throws Exception {
		// given
		RegisterRequestDto request = createRegisterRequestDto("TestUser", "TestName");
		performRequest("/owner/register", request, HttpMethod.POST);

		LoginRequestDto loginRequest = LoginRequestDto.builder()
			.userId("TestUser")
			.password("TestPassword")
			.build();

		// when
		MockHttpServletResponse response = performRequest("/owner/login", loginRequest, HttpMethod.POST);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		TokenResponseDto responseDto = objectMapper.readValue(response.getContentAsString(), TokenResponseDto.class);
		assertThat(responseDto.getAccessToken()).isNotEmpty();
		assertThat(responseDto.getRefreshToken()).isNotEmpty();
	}

	@Test
	@DisplayName("GET /owner/tokens - 소유자 토큰 정보 조회가 성공적으로 처리된다")
	void getOwnerTokens_shouldSucceed() throws Exception {
		// when
		MockHttpServletResponse response = performRequest("/owner/tokens", HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		TokenResponseDto tokenResponseDto = objectMapper.readValue(response.getContentAsString(), TokenResponseDto.class);
		assertThat(tokenResponseDto.getAccessToken()).isNotEmpty();
		assertThat(tokenResponseDto.getRefreshToken()).isNotEmpty();
	}

	@Test
	@DisplayName("PUT /owner/update/{ownerId} - 소유자 프로필 업데이트가 성공적으로 처리된다")
	void updateOwnerProfile_shouldSucceed() throws Exception {
		// given
		RegisterRequestDto request = createRegisterRequestDto("TestUser", "TestName");

		MockHttpServletResponse createResponse = performRequest("/owner/register", request, HttpMethod.POST);
		OwnerResponseDto responseDto = objectMapper.readValue(createResponse.getContentAsString(), OwnerResponseDto.class);
		Integer ownerId = responseDto.getId();

		UpdateProfileRequestDto updateRequest = createUpdateProfileRequestDto("UpdatedName");

		// when
		MockHttpServletResponse response = performRequest("/owner/update/" + ownerId, updateRequest, HttpMethod.PUT);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		OwnerResponseDto updatedResponse = objectMapper.readValue(response.getContentAsString(), OwnerResponseDto.class);
		assertThat(updatedResponse.getName()).isEqualTo("UpdatedName");
	}

	@Test
	@DisplayName("PUT /owner/update/password/{ownerId} - 소유자 비밀번호 업데이트가 성공적으로 처리된다")
	void updateOwnerPassword_shouldSucceed() throws Exception {
		// given
		RegisterRequestDto request = createRegisterRequestDto("TestUser", "TestName");
		MockHttpServletResponse createResponse = performRequest("/owner/register", request, HttpMethod.POST);
		OwnerResponseDto responseDto = objectMapper.readValue(createResponse.getContentAsString(), OwnerResponseDto.class);
		Integer ownerId = responseDto.getId();

		UpdatePasswordRequestDto updatePasswordRequest = UpdatePasswordRequestDto.builder()
			.currentPassword("TestPassword")
			.newPassword("UpdatedPassword")
			.build();

		// when
		MockHttpServletResponse response = performRequest("/owner/update/password/" + ownerId, updatePasswordRequest, HttpMethod.PUT);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	@DisplayName("GET /owner - 모든 소유자 정보 조회가 성공적으로 처리된다")
	void getAllOwners_shouldReturnAllOwners() throws Exception {
		// given
		RegisterRequestDto request1 = createRegisterRequestDto("TestUser1", "TestName1");
		RegisterRequestDto request2 = createRegisterRequestDto("TestUser2", "TestName2");

		performRequest("/owner/register", request1, HttpMethod.POST);
		performRequest("/owner/register", request2, HttpMethod.POST);

		// when
		MockHttpServletResponse response = performRequest("/owner", HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		OwnerResponseDto[] owners = objectMapper.readValue(response.getContentAsString(), OwnerResponseDto[].class);
		assertThat(owners).hasSize(2);
		assertThat(owners[0].getName()).isEqualTo("TestName1");
		assertThat(owners[1].getName()).isEqualTo("TestName2");
	}

	@Test
	@DisplayName("GET /owner/{ownerId} - 특정 소유자 정보 조회가 성공적으로 처리된다")
	void getOwnerById_shouldReturnOwnerDetails() throws Exception {
		// given
		RegisterRequestDto request = createRegisterRequestDto("TestUser", "TestName");

		MockHttpServletResponse createResponse = performRequest("/owner/register", request, HttpMethod.POST);
		OwnerResponseDto responseDto = objectMapper.readValue(createResponse.getContentAsString(), OwnerResponseDto.class);
		Integer ownerId = responseDto.getId();

		// when
		MockHttpServletResponse response = performRequest("/owner/" + ownerId, HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		OwnerResponseDto ownerResponseDto = objectMapper.readValue(response.getContentAsString(), OwnerResponseDto.class);
		assertThat(ownerResponseDto.getName()).isEqualTo("TestName");
	}

	private String generateTestToken() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("ownerId", 1);
		claims.put("role", "ROLE_USER");
		return jwtTokenHelper.issueAccessToken(claims).getToken();
	}

	private MockHttpServletResponse performRequest(String url, Object request, HttpMethod method) throws Exception {
		return mockMvc.perform(
			request(HttpMethod.valueOf(method.name()), url)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
				.content(objectMapper.writeValueAsString(request))
		).andReturn().getResponse();
	}

	private MockHttpServletResponse performRequest(String url, HttpMethod method) throws Exception {
		return mockMvc.perform(
			request(HttpMethod.valueOf(method.name()), url)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
		).andReturn().getResponse();
	}

	private RegisterRequestDto createRegisterRequestDto(String userId, String name) {
		return RegisterRequestDto.builder()
			.userId(userId)
			.password("TestPassword")
			.name(name)
			.address("Address")
			.city("City")
			.telephone("01012341234")
			.build();
	}

	private UpdateProfileRequestDto createUpdateProfileRequestDto(String name) {
		return UpdateProfileRequestDto.builder()
			.name(name)
			.address("UpdatedAddress")
			.city("UpdatedCity")
			.telephone("01098765432")
			.build();
	}
}
