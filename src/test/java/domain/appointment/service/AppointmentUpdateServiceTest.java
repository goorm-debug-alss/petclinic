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
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbagePetRepository;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbageVetRepository;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentUpdateService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
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
	private GarbagePetRepository petRepository;

	@Mock
	private GarbageVetRepository vetRepository;

	@InjectMocks
	private AppointmentUpdateService appointmentUpdateService;

	private AppointmentRequestDto requestDto;
	private Pet pet;
	private Vet vet;
	private Appointment appointment;

	@BeforeEach
	@DisplayName("테스트 데이터 초기화")
	void setUp() {
		requestDto = createTestAppointmentRequestDto();
		pet = createTestPet();
		vet = createTestVet();
		appointment = createTestAppointment();
	}

	@Test
	@DisplayName("예약 정보 업데이트 성공")
	void updateAppointment_Success() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));
		when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

		// when
		AppointmentResponseDto responseDto = appointmentUpdateService.updateAppointment(1, requestDto);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getBody().getPetName()).isEqualTo("구름이");
		assertThat(responseDto.getBody().getVetName()).isEqualTo("닥터 구름");

		verify(appointmentRepository).findById(1);
		verify(petRepository).findById(1);
		verify(vetRepository).findById(1);
		verify(appointmentRepository).save(any(Appointment.class));
	}

	@Test
	@DisplayName("예약 조회 실패 - Appointment ID가 존재하지 않을 때")
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
	@DisplayName("예약 조회 실패 - Pet ID가 존재하지 않을 때")
	void updateAppointment_PetNotFound() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
		when(petRepository.findById(1)).thenReturn(Optional.empty());

		// when & then
		assertThrows(PetNotFoundException.class, () ->
			appointmentUpdateService.updateAppointment(1, requestDto));

		verify(appointmentRepository).findById(1);
		verify(petRepository).findById(1);
		verify(vetRepository, never()).findById(anyInt());
	}

	@Test
	@DisplayName("예약 조회 실패 - Vet ID가 존재하지 않을 때")
	void updateAppointment_VetNotFound() {
		// given
		when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));
		when(vetRepository.findById(1)).thenReturn(Optional.empty());

		// when & then
		assertThrows(VetNotFoundException.class, () ->
			appointmentUpdateService.updateAppointment(1, requestDto));

		verify(petRepository, times(1)).findById(anyInt());
		verify(vetRepository).findById(1);
		verify(appointmentRepository).findById(1);
	}

	private AppointmentRequestDto createTestAppointmentRequestDto() {
		return AppointmentRequestDto.builder()
			.apptDate(LocalDate.now())
			.status(ApptStatus.COMPLETE)
			.symptoms("감기")
			.petId(1)
			.vetId(1)
			.build();
	}

	private Appointment createTestAppointment() {
		return Appointment.builder()
			.id(1)
			.apptDate(LocalDate.now())
			.status(ApptStatus.COMPLETE)
			.symptoms("감기")
			.build();
	}

	private Pet createTestPet() {
		return Pet.builder()
			.id(1)
			.name("구름이")
			.build();
	}

	private Vet createTestVet() {
		return Vet.builder()
			.id(1)
			.name("닥터 구름")
			.build();
	}
}
