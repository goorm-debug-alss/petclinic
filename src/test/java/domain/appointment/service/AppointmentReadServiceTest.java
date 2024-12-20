package domain.appointment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.ResultResponseDto;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentHelper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentReadService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

/**
 * AppointmentReadService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentReadServiceTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	private AppointmentHelper appointmentHelper = new AppointmentHelper();

	@InjectMocks
	private AppointmentReadService appointmentReadService;

	@Test
	@DisplayName("모든 예약 정보 조회 성공")
	void getAllAppointments_Success() {
		// given
		Appointment appointment1 = createMockAppointment(1, "구름이", "감기", LocalDateTime.now());
		Appointment appointment2 = createMockAppointment(2, "구르미", "몸살", LocalDateTime.now());
		when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

		// when
		ResultResponseDto<AppointmentResponseDto.Body> result = appointmentReadService.findALlAppointments();

		// then
		assertThat(result.getBody()).hasSize(2);
		verify(appointmentRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("예약 ID로 예약 조회 성공")
	void getAppointmentById_Success() {
		// given
		Appointment appointment = createMockAppointment(1, "구름이", "감기", LocalDateTime.now());
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

		// when
		AppointmentResponseDto.Body response = appointmentReadService.findAppointmentById(1);

		// then
		assertThat(response.getId()).isEqualTo(1);
		assertThat(response.getPetName()).isEqualTo("구름이");
		assertThat(response.getVetName()).isEqualTo("수의사");
		verify(appointmentRepository, times(1)).findById(1);
	}

	@Test
	@DisplayName("예약 ID로 예약 조회 실패 - 예외 발생")
	void getAppointmentById_Failure() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

		// when & then
		assertThrows(AppointmentNotFoundException.class, () -> appointmentReadService.findAppointmentById(1));
		verify(appointmentRepository, times(1)).findById(1);
	}

	private Appointment createMockAppointment(Integer id, String petName, String symptoms, LocalDateTime dateTime) {
		return Appointment.builder()
			.id(id)
			.petId(Pet.builder().name(petName).build())
			.vetId(Vet.builder().name("수의사").build())
			.symptoms(symptoms)
			.apptDateTime(dateTime)
			.build();
	}
}
