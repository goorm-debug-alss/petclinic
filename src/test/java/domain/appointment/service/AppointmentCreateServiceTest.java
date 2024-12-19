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
import org.springframework.samples.petclinic.domain.appointment.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentCreateService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

/**
 * AppointmentCreateServiceTest
 * <p>
 * 이 클래스는 AppointmentCreateService 기능을 테스트하기 위한 단위 테스트 클래스입니다.
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentCreateServiceTest {

	@InjectMocks
	private AppointmentCreateService appointmentCreateService;

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PetRepository petRepository;

	@Mock
	private VetRepository vetRepository;

	private AppointmentRequestDto requestDto;
	private Pet pet;
	private Vet vet;
	private Appointment appointment;

	@BeforeEach
	@DisplayName("테스트 데이터")
	void setUp() {
		createTestAppointmentRequestDto();
		createTestPet();
		createTestVet();
		createTestAppointment();
	}

	@Test
	@DisplayName("예약 생성 성공 - 모든 조건이 충족될 때")
	void createAppointment_Success() {
		// given
		when(petRepository.findById(requestDto.getPetId())).thenReturn(Optional.of(pet));
		when(vetRepository.findById(requestDto.getVetId())).thenReturn(Optional.of(vet));
		when(appointmentRepository.save(appointment)).thenReturn(appointment);

		// when
		AppointmentResponseDto savedAppointment = appointmentCreateService.createAppointment(requestDto);

		// Then
		assertThat(savedAppointment).isNotNull();
		assertThat(savedAppointment.getBody().getPetName()).isEqualTo(pet.getName());
		assertThat(savedAppointment.getBody().getVetName()).isEqualTo(vet.getName());
		assertThat(savedAppointment.getBody().getSymptoms()).isEqualTo(requestDto.getSymptoms());
	}

	@Test
	@DisplayName("예약 생성 실패 - Pet ID가 존재하지 않을 때")
	void createAppointment_PetNotFound() {
		// given
		when(petRepository.findById(requestDto.getPetId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(PetNotFoundException.class, () ->
				appointmentCreateService.createAppointment(requestDto));

		verify(petRepository).findById(requestDto.getPetId());
		verify(vetRepository, never()).findById(anyInt());
		verify(appointmentRepository, never()).save(any(Appointment.class));
	}

	@Test
	@DisplayName("에약 생성 실패 - Vet ID가 존재하지 않을 때")
	void createAppointment_VetNotFound() {
		// given
		when(petRepository.findById(requestDto.getPetId())).thenReturn(Optional.of(pet));
		when(vetRepository.findById(requestDto.getVetId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(VetNotFoundException.class, () ->
				appointmentCreateService.createAppointment(requestDto));

		verify(petRepository).findById(requestDto.getPetId());
		verify(vetRepository).findById(requestDto.getVetId());
		verify(appointmentRepository, never()).save(any(Appointment.class));
	}

	private void createTestAppointmentRequestDto() {
		requestDto = AppointmentRequestDto.builder()
				.petId(1)
				.vetId(1)
				.apptDateTime(LocalDateTime.now())
				.status(ApptStatus.valueOf("COMPLETE"))
				.symptoms("Coughing")
				.build();
	}

	private void createTestPet() {
		pet = Pet.builder()
				.id(1)
				.name("구름")
				.build();
	}

	private void createTestAppointment() {
		appointment = Appointment.builder()
				.id(1)
				.apptDateTime(requestDto.getApptDateTime())
				.status(requestDto.getStatus())
				.symptoms(requestDto.getSymptoms())
				.petId(pet)
				.vetId(vet)
				.build();
	}

	private void createTestVet() {
		vet = Vet.builder()
				.id(1)
				.name("구름수의사")
				.build();
	}
}
