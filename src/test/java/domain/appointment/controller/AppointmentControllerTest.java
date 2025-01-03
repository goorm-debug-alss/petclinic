package domain.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

	private String generateTestToken() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("ownerId", 1);
		claims.put("role", "ROLE_USER");
		return jwtTokenHelper.issueAccessToken(claims).getToken();
	}

	private static AppointmentRequestDto createAppointmentRequestDto(int vetId, int petId, String symptoms) {
		return AppointmentRequestDto.builder()
			.vetId(vetId)
			.petId(petId)
			.apptDateTime(LocalDateTime.of(2025, 12, 25, 12, 0 , 0))
			.appStatus(ApptStatus.COMPLETE)
			.symptoms(symptoms)
			.build();
	}

	@Test
	@DisplayName("POST / appointment - 예약 생성 시 유효한 데이터를 제공하면 예약이 성공적으로 생성된다")
	void createAppointment_shouldSucceedWhenValidDateProvided() throws Exception {
		// given: 유효한 예약 데이터 생성
		AppointmentRequestDto request = createAppointmentRequestDto(1, 5, "test");

		// when: 예약 생성 API 호출
		MockHttpServletResponse response = mockMvc.perform(post("/appointment")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 생성된 예약 ID가 유효한 값이어야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto appointmentResponseDto = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto.class);
		assertThat(appointmentResponseDto.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("GET /appointment - 모든 예약 조회 시 생성된 모든 예약 데이터가 반환된다")
	void getAllAppointments_shouldReturnAllAppointments() throws Exception {
		// given: 두 개의 예약 데이터를 생성
		AppointmentRequestDto request1 = createAppointmentRequestDto(1, 5, "Test Symptoms 1");
		AppointmentRequestDto request2 = createAppointmentRequestDto(2, 5, "Test Symptoms 2");

		mockMvc.perform(post("/appointment")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request1)))
			.andReturn();

		mockMvc.perform(post("/appointment")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request2)))
			.andReturn();

		// when: 모든 예약 조회 API 호출
		MockHttpServletResponse response = mockMvc.perform(get("/appointment")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 반환된 예약 데이터의 개수가 2개여야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto[] appointments = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto[].class);
		assertThat(appointments).hasSize(2);
		assertThat(appointments[0].getSymptoms()).isEqualTo("Test Symptoms 1");
		assertThat(appointments[1].getSymptoms()).isEqualTo("Test Symptoms 2");
	}

	@Test
	@DisplayName("GET /appointment/{appointmentId} - 특정 예약 조회 시 해당 예약 데이터가 반환된다")
	void getAppointmentById_shouldReturnSpecificAppointment() throws Exception {
		// given: 하나의 예약 데이터를 생성
		AppointmentRequestDto request = createAppointmentRequestDto(1, 5, "Test Symptoms 1");
		MockHttpServletResponse createResponse = mockMvc.perform(post("/appointment")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn().getResponse();

		AppointmentResponseDto createAppointment = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer appointmentId = createAppointment.getId();

		// when: 특정 예약 조회 API 호출
		MockHttpServletResponse response = mockMvc.perform(get("/appointment/{appointmentId}", appointmentId)
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 반환된 데이터가 예약 데이터와 일치해야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto appointmentResponseDto = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto.class);
		assertThat(appointmentResponseDto.getId()).isEqualTo(appointmentId);
		assertThat(appointmentResponseDto.getSymptoms()).isEqualTo("Test Symptoms 1");
		assertThat(appointmentResponseDto.getPetId()).isEqualTo(5);
	}

	@Test
	@DisplayName("PUT /appointment/{appointmentId} - 예약 데이터를 수정하면 업데이트된 데이터가 반환된다")
	void updateAppointment_shouldReturnUpdatedAppointment() throws Exception {
		// given: 하나의 예약 데이터를 생성 후 업데이트할 데이터 준비
		AppointmentRequestDto initialRequest = createAppointmentRequestDto(1, 5, "Initial Symptoms");
		MockHttpServletResponse createResponse = mockMvc.perform(post("/appointment")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(initialRequest)))
			.andReturn().getResponse();

		AppointmentResponseDto createAppointment = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer appointmentId = createAppointment.getId();

		AppointmentRequestDto updateRequest = createAppointmentRequestDto(1, 5, "Updated Symptoms");

		// when: 예약 수정 API 호출
		MockHttpServletResponse response = mockMvc.perform(put("/appointment/{appointmentId}", appointmentId)
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이고, 반환된 데이터가 업데이트된 데이터와 일치해야 한다
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		AppointmentResponseDto updatedAppointment = objectMapper.readValue(response.getContentAsString(), AppointmentResponseDto.class);
		assertThat(updatedAppointment.getId()).isEqualTo(appointmentId);
		assertThat(updatedAppointment.getSymptoms()).isEqualTo("Updated Symptoms");
	}

	@Test
	@DisplayName("DELETE /appointment/{appointmentId} - 예약을 삭제하면 성공적으로 처리된다")
	void deleteAppointment_shouldSucceedWhenValidIdProvided() throws Exception {
		// given: 하나의 예약 데이터를 생성
		AppointmentRequestDto request = createAppointmentRequestDto(1, 5, "Test Symptoms");
		MockHttpServletResponse createResponse = mockMvc.perform(post("/appointment")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn().getResponse();

		AppointmentResponseDto createAppointment = objectMapper.readValue(createResponse.getContentAsString(), AppointmentResponseDto.class);
		Integer appointmentId = createAppointment.getId();

		// when: 예약 삭제 API 호출
		MockHttpServletResponse deleteResponse = mockMvc.perform(delete("/appointment/{appointmentId}", appointmentId)
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// then: 응답 상태 코드가 200이어야 한다
		assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
	}
}
