package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.Result;
import org.springframework.samples.petclinic.domain.appointment.dto.StatusCode;
import org.springframework.samples.petclinic.domain.appointment.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentHelper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예약 생성 서비스
 * - 예약 생성 로직 및 데이터베이스 연동 처리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentCreateService {

	private final AppointmentRepository appointmentRepository;
	private final PetRepository petRepository;
	private final VetRepository vetRepository;

	/**
	 * 예약 생성
	 *
	 * @param dto 요청 데이터
	 * @return 생성된 예약 정보
     */
	public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
		Pet pet = fetchPetByIdOrThrow(dto);
		Vet vet = fetchVetByIdOrThrow(dto);

		Appointment appointment = convertToAppointmentEntity(dto, pet, vet);
		Appointment savedAppointment = appointmentRepository.save(appointment);

		return createResponseFromBody(savedAppointment, pet, vet);
	}

	private Pet fetchPetByIdOrThrow(AppointmentRequestDto dto) {
        return petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));
	}

	private Vet fetchVetByIdOrThrow(AppointmentRequestDto dto) {
        return vetRepository.findById(dto.getVetId())
                .orElseThrow(() -> new VetNotFoundException("Vet not found"));
	}

	private Appointment convertToAppointmentEntity(AppointmentRequestDto dto, Pet pet, Vet vet) {
        return Appointment.builder()
            .apptDateTime(dto.getApptDateTime())
            .status(dto.getStatus())
            .symptoms(dto.getSymptoms())
            .petId(pet)
            .vetId(vet)
            .build();
	}

	private AppointmentResponseDto createResponseFromBody(Appointment savedAppointment, Pet pet, Vet vet) {
		Result result = generateSuccessResult();
		AppointmentResponseDto.Body body = AppointmentHelper.toBody(savedAppointment, pet, vet);
		return createResponseFromBody(result, body);
	}

	private static Result generateSuccessResult() {
		return Result.builder()
				.resultCode(StatusCode.SUCCESS.getCode())
				.resultDescription(StatusCode.SUCCESS.getDescription())
				.build();
	}

	private static AppointmentResponseDto createResponseFromBody(Result result, AppointmentResponseDto.Body body) {
		return AppointmentResponseDto.builder()
				.result(result)
				.body(body)
				.build();
	}
}
