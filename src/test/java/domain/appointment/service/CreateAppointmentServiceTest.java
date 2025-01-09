package domain.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentMapper;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.domain.appointment.service.CreateAppointmentService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.service.VetService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateAppointmentServiceTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private VetService vetService;

	@Mock
	private PetRepository petRepository;

	@Mock
	private AppointmentMapper appointmentMapper;

	@InjectMocks
	private CreateAppointmentService createAppointmentService;

	private AppointmentRequestDto request;
	private Pet mockPet;
	private Vet mockVet;
	private Appointment mockAppointment;

	@BeforeEach
	void setUp() {
		createMockRequestTestData();
		createMockPetTestData();
		createMockVetTestData();
		createMockAppointmentTestData();
	}


	@Test
	@DisplayName("예약 생성 성공 - 유효한 요청 데이터를 제공하면 예약이 성공적으로 생성된다")
	void validRequestData_createAppointment_createSuccessfully() {
		// given
		when(vetService.getVetOrThrow(1)).thenReturn(mockVet);
		when(petRepository.findById(1)).thenReturn(Optional.of(mockPet));
		when(appointmentMapper.toEntity(request, mockPet, mockVet)).thenReturn(mockAppointment);
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

		// when
		Appointment appointment = createAppointmentService.createAppointment(request);

		// then
		assertThat(appointment.getId()).isEqualTo(mockAppointment.getId());
		assertThat(appointment.getPet()).isEqualTo(mockAppointment.getPet());
		assertThat(appointment.getVet()).isEqualTo(mockAppointment.getVet());
		assertThat(appointment.getSymptoms()).isEqualTo(mockAppointment.getSymptoms());
		assertThat(appointment.getApptDateTime()).isEqualTo(mockAppointment.getApptDateTime());
		assertThat(appointment.getStatus()).isEqualTo(mockAppointment.getStatus());
	}

	@Test
	@DisplayName("예약 생성 실패 - 유효하지 않은 수의사 ID가 제공되었을 때, 에러가 발생한다")
	void invalidVetId_createAppointment_throwsException() {
		// given
		when(vetService.getVetOrThrow(1)).thenThrow(new ApiException(VetErrorCode.NO_VET));

		// when & then
		assertThrows(ApiException.class, () -> createAppointmentService.createAppointment(request));
	}

	@Test
	@DisplayName("예약 생성 실패 - 유효하지 않은 동물 ID가 제공되었을 때, 에러가 발생한다")
	void invalidPetId_createAppointment_throwsException() {
		// given
		when(vetService.getVetOrThrow(1)).thenReturn(mockVet);
		when(petRepository.findById(1)).thenThrow(new ApiException(PetErrorCode.NO_PET));

		// when & then
		assertThrows(ApiException.class, () -> createAppointmentService.createAppointment(request));
	}

	@Test
	@DisplayName("예약 생성 실패 - 예약 날짜가 과거일 경우 예외가 발생한다")
	void pastAppointmentDate_createAppointment_throwsException() {
		// given
		request = AppointmentRequestDto.builder()
			.vetId(1)
			.petId(1)
			.apptDateTime(LocalDateTime.now().minusHours(1))
			.appStatus(ApptStatus.COMPLETE)
			.symptoms("test")
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createAppointmentService.createAppointment(request));
	}

	@Test
	@DisplayName("예약 생성 실패 - 동일 시간에 중복된 예약 요청이 있을 경우 예외가 발생한다")
	void duplicateAppointment_createAppointment_throwsException() {
		// given
		when(vetService.getVetOrThrow(1)).thenReturn(mockVet);
		when(petRepository.findById(1)).thenReturn(Optional.of(mockPet));
		when(appointmentRepository.existsByPetAndVetAndApptDateTime(mockPet, mockVet, request.getApptDateTime()))
			.thenReturn(true);

		// when & then
		assertThrows(ApiException.class, () -> createAppointmentService.createAppointment(request));
	}

	@Test
	@DisplayName("예약 생성 실패 - 예약 상태가 유효하지 않은 경우 예외가 발생한다")
	void invalidAppointmentStatus_createAppointment_throwsException() {
		// given
		request = AppointmentRequestDto.builder()
			.vetId(1)
			.petId(1)
			.apptDateTime(LocalDateTime.now().plusHours(1))
			.appStatus(null)
			.symptoms("test")
			.build();

		// when & then
		assertThrows(ApiException.class, () -> createAppointmentService.createAppointment(request));
	}

	private void createMockRequestTestData() {
		request = AppointmentRequestDto.builder()
			.vetId(1)
			.petId(1)
			.apptDateTime(LocalDateTime.of(2025, 12, 25, 12, 0 , 0))
			.appStatus(ApptStatus.COMPLETE)
			.symptoms("test")
			.build();
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
			.apptDateTime(request.getApptDateTime())
			.status(request.getAppStatus())
			.symptoms(request.getSymptoms())
			.build();
	}
}
