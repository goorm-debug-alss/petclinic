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
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * AppointmentUpdateServiceTest
 * <p>
 * 이 클래스는 AppointmentUpdateService 기능을 테스트하기 위한 단위 테스트 클래스입니다.
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentUpdateServiceTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PetRepository petRepository;

	@Mock
	private VetRepository vetRepository;

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
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
		when(petRepository.findById(requestDto.getPetId())).thenReturn(Optional.of(pet));
		when(vetRepository.findById(requestDto.getVetId())).thenReturn(Optional.of(vet));
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

		// when
		AppointmentResponseDto responseDto = appointmentUpdateService.updateAppointment(1, requestDto);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getBody().getPetName()).isEqualTo("구름");
		assertThat(responseDto.getBody().getVetName()).isEqualTo("닥터 구름");

		verify(appointmentRepository).findById(1);
		verify(petRepository).findById(requestDto.getPetId());
		verify(vetRepository).findById(requestDto.getVetId());
		verify(appointmentRepository).save(any(Appointment.class));
	}

	@Test
	@DisplayName("예약 업데이트 실패 - Appointment ID가 존재하지 않을 때")
	void updateAppointment_AppointmentNotFound() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

		// when & then
		assertThrows(AppointmentNotFoundException.class, () ->
				appointmentUpdateService.updateAppointment(1, requestDto));

		verify(appointmentRepository).findById(1);
		verify(petRepository, never()).findById(anyInt());
		verify(vetRepository, never()).findById(anyInt());
	}

	@Test
	@DisplayName("예약 업데이트 실패 - Pet ID가 존재하지 않을 때")
	void updateAppointment_PetNotFound() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
		when(petRepository.findById(requestDto.getPetId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(PetNotFoundException.class, () ->
				appointmentUpdateService.updateAppointment(1, requestDto));

		verify(appointmentRepository).findById(1);
		verify(petRepository).findById(requestDto.getPetId());
		verify(vetRepository, never()).findById(anyInt());
	}

	@Test
	@DisplayName("예약 업데이트 실패 - Vet ID가 존재하지 않을 때")
	void updateAppointment_VetNotFound() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
		when(petRepository.findById(requestDto.getPetId())).thenReturn(Optional.of(pet));
		when(vetRepository.findById(requestDto.getVetId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(VetNotFoundException.class, () ->
				appointmentUpdateService.updateAppointment(1, requestDto));

		verify(appointmentRepository).findById(1);
		verify(petRepository).findById(requestDto.getPetId());
		verify(vetRepository).findById(requestDto.getVetId());
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
