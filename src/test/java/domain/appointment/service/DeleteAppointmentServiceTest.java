package domain.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.DeleteAppointmentService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteAppointmentServiceTest {

	@InjectMocks
	private DeleteAppointmentService deleteAppointmentService;

	@Mock
	private AppointmentRepository appointmentRepository;

	private Pet mockPet;
	private Vet mockVet;
	private Appointment mockAppointment;

	@BeforeEach
	void setUp() {
		createMockPetTestData();
		createMockVetTestData();
		createMockAppointmentTestData();
	}

	@Test
	@DisplayName("예약 삭제 성공 - 유요한 예약 ID를 제공하면 예약이 성공적으로 삭제된다")
	void validId_deleteAppointment_deleteSuccessfully() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(mockAppointment));

		// when
		deleteAppointmentService.deleteAppointment(1);

		// then
		verify(appointmentRepository, times(1)).findById(1);
		verify(appointmentRepository, times(1)).delete(mockAppointment);
	}

	@Test
	@DisplayName("예약 삭제 실패 - 유효하지 않은 예약 ID가 제공되었을 때, 예외가 발생한다")
	void invalidId_deleteAppointment_throwsException() {
		// given
		when(appointmentRepository.findById(1)).thenThrow(IllegalArgumentException.class);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> deleteAppointmentService.deleteAppointment(mockAppointment.getId()));
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

	private void createMockAppointmentTestData() {
		mockAppointment = Appointment.builder()
			.id(1)
			.pet(mockPet)
			.vet(mockVet)
			.apptDateTime(LocalDateTime.now())
			.status(ApptStatus.COMPLETE)
			.symptoms("test")
			.build();
	}
}
