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
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentUpdateService;
import org.springframework.samples.petclinic.domain.appointment.service.EntityRetrievalService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentUpdateServiceTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private EntityRetrievalService entityRetrievalService;

	@InjectMocks
	private AppointmentUpdateService appointmentUpdateService;

	private AppointmentRequestDto requestDto;
	private Appointment appointment;
	private Pet pet;
	private Vet vet;

	@BeforeEach
	@DisplayName("테스트 데이터 초기화")
	void setUp() {
		requestDto = createTestAppointmentRequestDto();
		pet = createTestPet();
		vet = createTestVet();
		appointment = createTestAppointment();
	}

	@Test
	@DisplayName("예약 업데이트 성공")
	void updateAppointment_Success() {
		// given
		when(entityRetrievalService.fetchAppointmentByIdOrThrow(1)).thenReturn(appointment);
		when(entityRetrievalService.fetchPetByIdOrThrow(requestDto.getPetId())).thenReturn(pet);
		when(entityRetrievalService.fetchVetByIdOrThrow(requestDto.getVetId())).thenReturn(vet);
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

		// when
		AppointmentResponseDto responseDto = appointmentUpdateService.updateAppointment(1, requestDto);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getPetName()).isEqualTo("구름");
		assertThat(responseDto.getVetName()).isEqualTo("닥터 구름");
		assertThat(responseDto.getSymptoms()).isEqualTo("감기");
		assertThat(responseDto.getStatus()).isEqualTo(ApptStatus.COMPLETE);

		verify(entityRetrievalService).fetchAppointmentByIdOrThrow(1);
		verify(entityRetrievalService).fetchPetByIdOrThrow(requestDto.getPetId());
		verify(entityRetrievalService).fetchVetByIdOrThrow(requestDto.getVetId());
		verify(appointmentRepository).save(any(Appointment.class));
	}

	@Test
	@DisplayName("예약 업데이트 실패 - Appointment ID가 존재하지 않을 때")
	void updateAppointment_AppointmentNotFound() {
		// given
		when(entityRetrievalService.fetchAppointmentByIdOrThrow(1)).thenThrow(new AppointmentNotFoundException(1));

		// when & then
		assertThrows(AppointmentNotFoundException.class, () ->
			appointmentUpdateService.updateAppointment(1, requestDto));

		verify(entityRetrievalService).fetchAppointmentByIdOrThrow(1);
	}

	@Test
	@DisplayName("예약 업데이트 실패 - Pet ID가 존재하지 않을 때")
	void updateAppointment_PetNotFound() {
		// given
		when(entityRetrievalService.fetchAppointmentByIdOrThrow(1)).thenReturn(appointment);
		when(entityRetrievalService.fetchPetByIdOrThrow(requestDto.getPetId())).thenThrow(new PetNotFoundException(requestDto.getPetId()));

		// when & then
		assertThrows(PetNotFoundException.class, () ->
			appointmentUpdateService.updateAppointment(1, requestDto));

		verify(entityRetrievalService).fetchAppointmentByIdOrThrow(1);
		verify(entityRetrievalService).fetchPetByIdOrThrow(requestDto.getPetId());
	}

	@Test
	@DisplayName("예약 업데이트 실패 - Vet ID가 존재하지 않을 때")
	void updateAppointment_VetNotFound() {
		// given
		when(entityRetrievalService.fetchAppointmentByIdOrThrow(1)).thenReturn(appointment);
		when(entityRetrievalService.fetchPetByIdOrThrow(requestDto.getPetId())).thenReturn(pet);
		when(entityRetrievalService.fetchVetByIdOrThrow(requestDto.getVetId())).thenThrow(new VetNotFoundException(requestDto.getVetId()));

		// when & then
		assertThrows(VetNotFoundException.class, () ->
			appointmentUpdateService.updateAppointment(1, requestDto));

		verify(entityRetrievalService).fetchAppointmentByIdOrThrow(1);
		verify(entityRetrievalService).fetchPetByIdOrThrow(requestDto.getPetId());
		verify(entityRetrievalService).fetchVetByIdOrThrow(requestDto.getVetId());
	}

	private AppointmentRequestDto createTestAppointmentRequestDto() {
		return AppointmentRequestDto.builder()
			.apptDateTime(LocalDateTime.now())
			.status(ApptStatus.COMPLETE)
			.symptoms("감기")
			.petId(1)
			.vetId(1)
			.build();
	}

	private Appointment createTestAppointment() {
		return Appointment.builder()
			.id(1)
			.apptDateTime(LocalDateTime.now())
			.status(ApptStatus.COMPLETE)
			.symptoms("감기")
			.petId(pet)
			.vetId(vet)
			.build();
	}

	private Pet createTestPet() {
		return Pet.builder()
			.id(1)
			.name("구름")
			.build();
	}

	private Vet createTestVet() {
		return Vet.builder()
			.id(1)
			.name("닥터 구름")
			.build();
	}
}
