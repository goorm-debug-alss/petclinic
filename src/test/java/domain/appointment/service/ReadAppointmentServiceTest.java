package domain.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentMapper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.ReadAppointmentService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadAppointmentServiceTest {

	@InjectMocks
	private ReadAppointmentService readAppointmentService;

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private AppointmentMapper appointmentMapper;

	private static Pet mockPet;
	private static Vet mockVet;
	private Appointment mockAppointment1;
	private Appointment mockAppointment2;
	private AppointmentResponseDto mockResponse1;
	private AppointmentResponseDto mockResponse2;

	@BeforeEach
	void setUp() {
		createMockPetTestData();
		createMockVetTestData();
		mockAppointment1 = createMockAppointment("Test Symptoms 1");
		mockAppointment2 = createMockAppointment("Test Symptoms 2");
		mockResponse1 = createMockResponseDto(1, "Test Symptoms 1");
		mockResponse2 = createMockResponseDto(2, "Test Symptoms 2");
	}

	@Test
	@DisplayName("모든 예약 조회 성공 - 예약 데이터가 존재할 때, 모든 예약 데이터를 반환한다")
	void appointmentsExist_findAllAppointments_returnsAppointmentList() {
		// given
		when(appointmentRepository.findAll()).thenReturn(List.of(mockAppointment1, mockAppointment2));
		when(appointmentMapper.toDto(mockAppointment1)).thenReturn(mockResponse1);
		when(appointmentMapper.toDto(mockAppointment2)).thenReturn(mockResponse2);

		// when
		List<AppointmentResponseDto> result = readAppointmentService.findAllAppointments();

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getSymptoms()).isEqualTo("Test Symptoms 1");
		assertThat(result.get(1).getSymptoms()).isEqualTo("Test Symptoms 2");
	}

	@Test
	@DisplayName("특정 예약 조회 성공 - 유요한 예약 ID로 조회를 시도하면 해당 예약 데이터를 반환한다")
	void validAppointmentId_findAppointment_returnAppointmentDetails() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(mockAppointment1));
		when(appointmentMapper.toDto(mockAppointment1)).thenReturn(mockResponse1);

		AppointmentResponseDto response = readAppointmentService.findAppointment(1);

		assertThat(response.getId()).isEqualTo(1);
		assertThat(response.getSymptoms()).isEqualTo("Test Symptoms 1");
	}

	@Test
	@DisplayName("조회 실패 - 유효하지 않은 예약 ID로 조회를 시도하면 IllegalArgumentException이 발생한다")
	void validAppointmentId_findAppointment_throwsIllegalArgumentException() {
		// given
		when(appointmentRepository.findById(1)).thenThrow(IllegalArgumentException.class);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> readAppointmentService.findAppointment(mockAppointment1.getId()));
	}

	private void createMockPetTestData() {
		mockPet = Pet.builder()
			.id(1)
			.name("test pet")
			.build();
	}

	private void createMockVetTestData() {
		mockVet = Vet.builder()
			.id(1)
			.name("test vet")
			.build();
	}

	private static Appointment createMockAppointment(String symptoms) {
		return Appointment.builder()
			.id(1)
			.pet(mockPet)
			.vet(mockVet)
			.apptDateTime(LocalDateTime.now())
			.status(ApptStatus.COMPLETE)
			.symptoms(symptoms)
			.build();
	}

	private AppointmentResponseDto createMockResponseDto(Integer id, String symptoms) {
		return AppointmentResponseDto.builder()
			.id(id)
			.symptoms(symptoms)
			.build();
	}
}
