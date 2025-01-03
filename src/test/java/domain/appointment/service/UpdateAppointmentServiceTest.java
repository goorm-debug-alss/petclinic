package domain.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentMapper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.UpdateAppointmentService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateAppointmentServiceTest {

	@InjectMocks
	private UpdateAppointmentService updateAppointmentService;

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PetRepository petRepository;

	@Mock
	private VetRepository vetRepository;

	@Mock
	private AppointmentMapper appointmentMapper;

	private Appointment existingAppointment;
	private static Pet mockPet;
	private static Vet mockVet;
	private AppointmentResponseDto mockResponse;

	@BeforeEach
	void setUp() {
		createMockPetTestData();
		createMockVetTestData();
		createMockAppointmentTestData();
		createMockResponseTestData();
	}

	@Test
	@DisplayName("예약 업데이트 성공 - 유효한 요청 데이터와 ID가 제공되었을 때, 예약이 성공적으로 업데이트된다")
	void validRequestAndId_updateAppointment_updatesSuccessfully() {
		// given
		AppointmentRequestDto updateRequest = createUpdateRequest();
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(existingAppointment));
		when(petRepository.findById(1)).thenReturn(Optional.of(existingAppointment.getPet()));
		when(vetRepository.findById(1)).thenReturn(Optional.of(existingAppointment.getVet()));
		when(appointmentRepository.save(existingAppointment)).thenAnswer(invocation -> invocation.getArgument(0));
		when(appointmentMapper.toDto(existingAppointment)).thenReturn(mockResponse);

		// when
		AppointmentResponseDto response = updateAppointmentService.updateAppointment(1, updateRequest);

		// then
		assertThat(response.getId()).isEqualTo(1);
		assertThat(response.getSymptoms()).isEqualTo("Updated Symptoms");
	}

	@Test
	@DisplayName("예약 업데이트 실패 - 유효하지 않은 예약 ID가 제공되었을 때, 예외가 발생한다")
	void invalidAppointmentId_updateAppointment_throwsException() {
		// given
		when(appointmentRepository.findById(1)).thenThrow(IllegalArgumentException.class);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> updateAppointmentService.updateAppointment(1, createUpdateRequest()));
	}

	@Test
	@DisplayName("예약 업데이트 실패 - 유효하지 않은 동물 ID가 제공되었을 때, 예외가 발생한다")
	void invalidPetId_updateAppointment_throwsException() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(existingAppointment));
		when(petRepository.findById(1)).thenThrow(IllegalArgumentException.class);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> updateAppointmentService.updateAppointment(1, createUpdateRequest()));
	}

	@Test
	@DisplayName("예약 업데이트 실패 - 유효하지 않은 수의사 ID가 제공되었을 때, 예외가 발생한다")
	void invalidId_updateAppointment_throwsException() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(existingAppointment));
		when(petRepository.findById(1)).thenReturn(Optional.of(mockPet));
		when(vetRepository.findById(1)).thenThrow(IllegalArgumentException.class);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> updateAppointmentService.updateAppointment(1, createUpdateRequest()));
	}

	private static AppointmentRequestDto createUpdateRequest() {
		return AppointmentRequestDto.builder()
			.vetId(1)
			.petId(1)
			.apptDateTime(LocalDateTime.of(2025, 12, 25, 12, 0 , 0))
			.appStatus(ApptStatus.COMPLETE)
			.symptoms("Updated Symptoms")
			.build();
	}

	private static void createMockPetTestData() {
		mockPet = Pet.builder()
			.id(1)
			.name("test pet")
			.build();
	}

	private static void createMockVetTestData() {
		mockVet = Vet.builder()
			.id(1)
			.name("test vet")
			.build();
	}

	private void createMockAppointmentTestData() {
		existingAppointment = Appointment.builder()
			.id(1)
			.vet(mockVet)
			.pet(mockPet)
			.apptDateTime(LocalDateTime.now())
			.status(ApptStatus.COMPLETE)
			.symptoms("Initial Symptoms")
			.build();
	}

	private void createMockResponseTestData() {
		mockResponse = AppointmentResponseDto.builder()
			.id(existingAppointment.getId())
			.apptDateTime(existingAppointment.getApptDateTime())
			.status(existingAppointment.getStatus())
			.symptoms("Updated Symptoms")
			.vetId(existingAppointment.getVet().getId())
			.petId(existingAppointment.getPet().getId())
			.build();
	}
}
