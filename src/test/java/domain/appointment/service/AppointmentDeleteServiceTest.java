package domain.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentDeleteService;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

/**
 * AppointmentDeleteServiceTest 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentDeleteServiceTest {

	@InjectMocks
	private AppointmentDeleteService appointmentDeleteService;

	@Mock
	private AppointmentRepository appointmentRepository;

	private Integer appointmentId;

	@BeforeEach
	void setUp() {
		appointmentId = 1;
	}

	@Test
	@DisplayName("예약 삭제 성공")
	void deleteAppointment_Success() {
		// given
		Appointment appointment = Appointment.builder()
			.id(appointmentId)
			.build();

		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

		// when
		appointmentDeleteService.deleteAppointment(appointmentId);

		// then
		verify(appointmentRepository).findById(appointmentId);
		verify(appointmentRepository).delete(appointment);
	}

	@Test
	@DisplayName("예약 삭제 실패 - 예약 ID가 존재하지 않을 때")
	void deleteAppointment_NotFound() {
		// given
		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(AppointmentNotFoundException.class, () ->
			appointmentDeleteService.deleteAppointment(appointmentId));

		verify(appointmentRepository).findById(appointmentId);
		verify(appointmentRepository, never()).delete(any());
	}
}
