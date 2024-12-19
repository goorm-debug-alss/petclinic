package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.Result;
import org.springframework.samples.petclinic.domain.appointment.dto.StatusCode;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbagePetRepository;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbageVetRepository;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 예약 정보를 업데이트하는 서비스 클래스입니다.
 * <p>
 * 예약 데이터를 수정하고 저장하며, 관련 엔티티를 검증 및 조회하는 로직을 포함합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentUpdateService {

	private final AppointmentRepository appointmentRepository;
	private final GarbagePetRepository petRepository;
	private final GarbageVetRepository vetRepository;

	/**
	 * 예약 정보를 업데이트하고, 결과를 응답 DTO로 반환합니다.
	 *
	 * @param appointmentId 업데이트할 예약의 ID
	 * @param dto 예약 업데이트 요청 데이터
	 * @return 업데이트된 예약 정보를 포함하는 응답 DTO
	 */
	public AppointmentResponseDto updateAppointment(Integer appointmentId, AppointmentRequestDto dto) {
		Appointment appointment = getAppointmentByIdOrThrow(appointmentId);
		Pet pet = getPetByIdOrThrow(dto);
		Vet vet = getVetByIdOrThrow(dto);

		updateAppointmentFields(dto, appointment, pet, vet);

		Appointment updatedAppointment = saveUpdateAppointment(appointment);

		return buildUpdatedAppointmentResponse(updatedAppointment, appointment);
	}

	/**
	 * 예약 ID로 예약을 조회하고, 없으면 예외를 던집니다.
	 *
	 * @param appointmentId 조회할 예약의 ID
	 * @return 조회된 예약 엔티티
	 * @throws AppointmentNotFoundException 예약을 찾을 수 없을 경우 발생
	 */
	private Appointment getAppointmentByIdOrThrow(Integer appointmentId) {
		return appointmentRepository.findById((appointmentId))
			.orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
	}

	/**
	 * 요청 DTO에서 Pet ID를 가져와 Pet 엔티티를 조회하고, 없으면 예외를 던집니다.
	 *
	 * @param dto 예약 요청 DTO
	 * @return 조회된 Pet 엔티티
	 * @throws PetNotFoundException Pet을 찾을 수 없을 경우 발생
	 */
	private Pet getPetByIdOrThrow(AppointmentRequestDto dto) {
		return petRepository.findById(dto.getPetId())
			.orElseThrow(() -> new PetNotFoundException("Pet not found"));
	}

	/**
	 * 요청 DTO에서 Vet ID를 가져와 Vet 엔티티를 조회하고, 없으면 예외를 던집니다.
	 *
	 * @param dto 예약 요청 DTO
	 * @return 조회된 Vet 엔티티
	 * @throws VetNotFoundException Vet을 찾을 수 없을 경우 발생
	 */
	private Vet getVetByIdOrThrow(AppointmentRequestDto dto) {
		return vetRepository.findById(dto.getVetId())
			.orElseThrow(() -> new VetNotFoundException("Vet not found"));
	}

	/**
	 * 예약 엔티티의 필드를 업데이트합니다.
	 *
	 * @param dto 예약 요청 DTO
	 * @param appointment 업데이트할 예약 엔티티
	 * @param pet 예약에 연관된 Pet 엔티티
	 * @param vet 예약에 연관된 Vet 엔티티
	 */
	private static void updateAppointmentFields(AppointmentRequestDto dto, Appointment appointment, Pet pet, Vet vet) {
		appointment.updateAppointment(
			dto.getApptDate(),
			dto.getStatus(),
			dto.getSymptoms(),
			pet,
			vet
		);
	}

	/**
	 * 수정된 예약 정보를 데이터베이스에 저장합니다.
	 *
	 * @param appointment 수정된 예약 엔티티
	 * @return 저장된 예약 엔티티
	 */
	private Appointment saveUpdateAppointment(Appointment appointment) {
		return appointmentRepository.save(appointment);
	}

	/**
	 * 업데이트된 예약 정보를 기반으로 응답 DTO를 생성합니다.
	 *
	 * @param updatedAppointment 업데이트된 예약 엔티티
	 * @param appointment 기존 예약 엔티티
	 * @return 생성된 응답 DTO
	 */
	private static AppointmentResponseDto buildUpdatedAppointmentResponse(Appointment updatedAppointment, Appointment appointment) {
		return AppointmentResponseDto.builder()
			.result(Result.builder()
				.resultCode(StatusCode.SUCCESS.getCode())
				.resultDescription(StatusCode.SUCCESS.getDescription())
				.build())
			.body(AppointmentResponseDto.Body.builder()
				.id(updatedAppointment.getId())
				.petName(appointment.getPetId().getName())
				.vetName(appointment.getVetId().getName())
				.apptDate(updatedAppointment.getApptDate())
				.status(updatedAppointment.getStatus())
				.symptoms(updatedAppointment.getSymptoms())
				.build())
			.build();
	}
}
