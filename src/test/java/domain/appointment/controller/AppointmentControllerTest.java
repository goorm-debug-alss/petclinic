package domain.appointment.controller;

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
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.token.helper.JwtTokenHelper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = PetClinicApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AppointmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	private String token;

	@BeforeEach
	void setUp() {
		appointmentRepository.deleteAll();
		token = generateTestToken();
	}

	@Test
	@DisplayName("POST /appointment - 예약 생성 시 유효한 데이터를 제공하면 예약이 성공적으로 생성된다")
	void createAppointment_shouldSucceedWhenValidDataProvided() throws Exception {
		// given
		AppointmentRequestDto request = createAppointmentRequestDto(1, 5, "test");

		// when
		MockHttpServletResponse response = performRequest("/appointment", request, HttpMethod.POST);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto appointmentResponseDto = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto.class);
		assertThat(appointmentResponseDto.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("GET /appointment - 모든 예약 조회 시 생성된 모든 예약 데이터가 반환된다")
	void getAllAppointments_shouldReturnAllAppointments() throws Exception {
		// given
		AppointmentRequestDto request1 = createAppointmentRequestDto(1, 5, "Test Symptoms 1");
		AppointmentRequestDto request2 = createAppointmentRequestDto(2, 5, "Test Symptoms 2");

		performRequest("/appointment", request1, HttpMethod.POST);
		performRequest("/appointment", request2, HttpMethod.POST);

		// when
		MockHttpServletResponse response = performRequest("/appointment", null, HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto[] appointments = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto[].class);
		assertThat(appointments).hasSize(2);
		assertThat(appointments[0].getSymptoms()).isEqualTo("Test Symptoms 1");
		assertThat(appointments[1].getSymptoms()).isEqualTo("Test Symptoms 2");
	}

	@Test
	@DisplayName("GET /appointment/{appointmentId} - 특정 예약 조회 시 해당 예약 데이터가 반환된다")
	void getAppointmentById_shouldReturnSpecificAppointment() throws Exception {
		// given
		AppointmentRequestDto request = createAppointmentRequestDto(1, 5, "Test Symptoms 1");
		MockHttpServletResponse createResponse = performRequest("/appointment", request, HttpMethod.POST);

		AppointmentResponseDto createdAppointment = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer appointmentId = createdAppointment.getId();

		// when
		MockHttpServletResponse response = performRequest("/appointment/" + appointmentId, null, HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto appointmentResponseDto = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto.class);
		assertThat(appointmentResponseDto.getId()).isEqualTo(appointmentId);
		assertThat(appointmentResponseDto.getSymptoms()).isEqualTo("Test Symptoms 1");
	}

	@Test
	@DisplayName("PUT /appointment/{appointmentId} - 예약 데이터를 수정하면 업데이트된 데이터가 반환된다")
	void updateAppointment_shouldReturnUpdatedAppointment() throws Exception {
		// given
		AppointmentRequestDto initialRequest = createAppointmentRequestDto(1, 5, "Initial Symptoms");
		MockHttpServletResponse createResponse = performRequest("/appointment", initialRequest, HttpMethod.POST);

		AppointmentResponseDto createdAppointment = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer appointmentId = createdAppointment.getId();

		AppointmentRequestDto updateRequest = createAppointmentRequestDto(1, 5, "Updated Symptoms");

		// when
		MockHttpServletResponse response = performRequest("/appointment/" + appointmentId, updateRequest, HttpMethod.PUT);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto updatedAppointment = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto.class);
		assertThat(updatedAppointment.getId()).isEqualTo(appointmentId);
		assertThat(updatedAppointment.getSymptoms()).isEqualTo("Updated Symptoms");
	}

	@Test
	@DisplayName("DELETE /appointment/{appointmentId} - 예약을 삭제하면 성공적으로 처리된다")
	void deleteAppointment_shouldSucceedWhenValidIdProvided() throws Exception {
		// given
		AppointmentRequestDto request = createAppointmentRequestDto(1, 5, "Test Symptoms");
		MockHttpServletResponse createResponse = performRequest("/appointment", request, HttpMethod.POST);

		AppointmentResponseDto createdAppointment = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer appointmentId = createdAppointment.getId();

		// when
		MockHttpServletResponse deleteResponse = performRequest("/appointment/" + appointmentId, null, HttpMethod.DELETE);

		// then
		assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
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
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(request != null ? objectMapper.writeValueAsString(request) : "")
		).andReturn().getResponse();
	}

	private static AppointmentRequestDto createAppointmentRequestDto(int vetId, int petId, String symptoms) {
		return AppointmentRequestDto.builder()
			.vetId(vetId)
			.petId(petId)
			.apptDateTime(LocalDateTime.of(2025, 12, 25, 12, 0, 0))
			.appStatus(ApptStatus.COMPLETE)
			.symptoms(symptoms)
			.build();
	}
}
