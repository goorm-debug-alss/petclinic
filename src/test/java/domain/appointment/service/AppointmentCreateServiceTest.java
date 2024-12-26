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
import org.springframework.samples.petclinic.domain.appointment.service.EntityRetrievalService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

/**
 * AppointmentCreateService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentCreateServiceTest {

	@InjectMocks
	private AppointmentCreateService appointmentCreateService;

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private EntityRetrievalService entityRetrievalService;

	private AppointmentRequestDto requestDto;
	private Pet mockPet;
	private Vet mockVet;
	private Appointment mockAppointment;

	@BeforeEach
	@DisplayName("테스트 데이터 초기화")
	void setUp() {
		requestDto = createTestAppointmentRequestDto();
		mockPet = createTestPet();
		mockVet = createTestVet();
		mockAppointment = createTestAppointment();
	}

	@Test
	@DisplayName("예약 생성 성공")
	void createAppointment_Success() {
		// given
		when(entityRetrievalService.fetchPetByIdOrThrow(requestDto.getPetId())).thenReturn(mockPet);
		when(entityRetrievalService.fetchVetByIdOrThrow(requestDto.getVetId())).thenReturn(mockVet);
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

		// when
		AppointmentResponseDto responseDto = appointmentCreateService.createAppointment(requestDto);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getPetName()).isEqualTo("구름");
		assertThat(responseDto.getVetName()).isEqualTo("닥터 구름");
		assertThat(responseDto.getSymptoms()).isEqualTo("감기");
		assertThat(responseDto.getStatus()).isEqualTo(ApptStatus.COMPLETE);

		verify(entityRetrievalService).fetchPetByIdOrThrow(requestDto.getPetId());
		verify(entityRetrievalService).fetchVetByIdOrThrow(requestDto.getVetId());
		verify(appointmentRepository).save(any(Appointment.class));
	}

	@Test
	@DisplayName("예약 생성 실패 - Pet ID가 존재하지 않을 때")
	void createAppointment_PetNotFound() {
		// given
		when(entityRetrievalService.fetchPetByIdOrThrow(requestDto.getPetId())).thenThrow(new PetNotFoundException(requestDto.getPetId()));

		// when & then
		assertThrows(PetNotFoundException.class, () -> appointmentCreateService.createAppointment(requestDto));

		verify(entityRetrievalService).fetchPetByIdOrThrow(requestDto.getPetId());
		verify(entityRetrievalService, never()).fetchVetByIdOrThrow(anyInt());
		verify(appointmentRepository, never()).save(any(Appointment.class));
	}

	@Test
	@DisplayName("예약 생성 실패 - Vet ID가 존재하지 않을 때")
	void createAppointment_VetNotFound() {
		// given
		when(entityRetrievalService.fetchPetByIdOrThrow(requestDto.getPetId())).thenReturn(mockPet);
		when(entityRetrievalService.fetchVetByIdOrThrow(requestDto.getVetId())).thenThrow(new VetNotFoundException(requestDto.getVetId()));

		// when & then
		assertThrows(VetNotFoundException.class, () -> appointmentCreateService.createAppointment(requestDto));

		verify(entityRetrievalService).fetchPetByIdOrThrow(requestDto.getPetId());
		verify(entityRetrievalService).fetchVetByIdOrThrow(requestDto.getVetId());
		verify(appointmentRepository, never()).save(any(Appointment.class));
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
			.petId(mockPet)
			.vetId(mockVet)
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
