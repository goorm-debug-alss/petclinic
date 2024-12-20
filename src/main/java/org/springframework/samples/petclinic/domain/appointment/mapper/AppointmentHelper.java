package org.springframework.samples.petclinic.domain.appointment.mapper;

import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.Result;
import org.springframework.samples.petclinic.domain.appointment.dto.StatusCode;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

/**
 * 예약 엔티티와 관련된 유틸리티 메서드를 제공
 */
public class AppointmentHelper {
	/**
	 * 기존 예약 엔티티의 필드를 전달 받은 DTO와 관련 엔티티로 업데이트
	 *
	 * @param dto			업데이트할 예약 데이터가 담긴 DTO
	 * @param appointment 	업데이트할 예약 엔티티
	 * @param pet			예약에 연결된 Pet 엔티티
	 * @param vet			예약에 연결된 Vet 엔티티
	 */
	public static void updateFields(AppointmentRequestDto dto, Appointment appointment, Pet pet, Vet vet) {
		appointment.updateAppointment(
			dto.getApptDateTime(),
			dto.getStatus(),
			dto.getSymptoms(),
			pet,
			vet
		);
	}

	/**
	 * 약 엔티티를 기반으로 응답 DTO를 생성
	 *
	 * @param appointment 응답으로 변환할 예약 엔티티
	 * @return 생성된 AppointmentResponseDto
	 */
	public static AppointmentResponseDto buildResponseDto(Appointment appointment) {
		return AppointmentResponseDto.builder()
			.result(Result.builder()
				.resultCode(StatusCode.SUCCESS.getCode())
				.resultDescription(StatusCode.SUCCESS.getDescription())
				.build())
			.body(buildBody(appointment))
			.build();
	}

	/**
	 * 예약 엔티티를 기반으로 Body 객체를 생성
	 *
	 * @param appointment 응답 Body로 변환할 예약 엔티티
	 * @return 생성된 AppointmentResponseDto.Body 객체
	 */
	public static AppointmentResponseDto.Body buildBody(Appointment appointment) {
		return AppointmentResponseDto.Body.builder()
			.id(appointment.getId())
			.petName(appointment.getPetId().getName())
			.vetName(appointment.getVetId().getName())
			.apptDateTime(appointment.getApptDateTime())
			.status(appointment.getStatus())
			.symptoms(appointment.getSymptoms())
			.build();
	}

	/**
	 * 예약, Pet, Vet 엔티티를 기반으로 Body 객체를 생성
	 * Pet 또는 Vet 정보가 null일 경우 예약 엔티티의 기본 정보를 사용
	 *
	 * @param appointment 	예약 엔티티
	 * @param pet			Pet 엔티티
	 * @param vet			vet 엔티티
	 * @return 생성된 AppointmentResponseDto.Body 객체
	 */
	public static AppointmentResponseDto.Body toBody(Appointment appointment, Pet pet, Vet vet) {
		return AppointmentResponseDto.Body.builder()
			.id(appointment.getId())
			.apptDateTime(appointment.getApptDateTime())
			.status(appointment.getStatus())
			.symptoms(appointment.getSymptoms())
			.petName(pet != null ? pet.getName() : appointment.getPetId().getName())
			.vetName(vet != null ? vet.getName() : appointment.getVetId().getName())
			.build();
	}
}
