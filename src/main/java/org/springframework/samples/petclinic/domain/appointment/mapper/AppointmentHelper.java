package org.springframework.samples.petclinic.domain.appointment.mapper;

import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
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
	public static void updateAppointmentFromDto(AppointmentRequestDto dto, Appointment appointment, Pet pet, Vet vet) {
		appointment.updateAppointment(
			dto.getApptDateTime(),
			dto.getStatus(),
			dto.getSymptoms(),
			pet,
			vet
		);
	}

	/**
	 * 예약 엔티티를 기반으로 응답 DTO를 생성
	 *
	 * @param appointment 응답으로 변환할 예약 엔티티
	 * @return 변환된 AppointmentResponseDto
	 */
	public static AppointmentResponseDto convertToResponse(Appointment appointment) {
		return mapToResponseDto(appointment, appointment.getPetId(), appointment.getVetId());
	}

	/**
	 * 예약, Pet, Vet 엔티티를 기반으로 Body 객체를 생성
	 * Pet 또는 Vet 정보가 null일 경우 예약 엔티티의 기본 정보를 사용
	 *
	 * @param appointment 	예약 엔티티
	 * @param pet			Pet 엔티티
	 * @param vet			vet 엔티티
	 * @return 변환된 AppointmentResponseDto
	 */
	public static AppointmentResponseDto createResponseDto(Appointment appointment, Pet pet, Vet vet) {
		return AppointmentResponseDto.builder()
			.id(appointment.getId())
			.apptDateTime(appointment.getApptDateTime())
			.status(appointment.getStatus())
			.symptoms(appointment.getSymptoms())
			.petName(pet != null ? pet.getName() : appointment.getPetId().getName())
			.vetName(vet != null ? vet.getName() : appointment.getVetId().getName())
			.build();
	}


	/**
	 * 예약, Pet, Vet 엔티티를 기반으로 응답 DTO를 생성하는 내부 메서드
	 *
	 * @param dto 요청 데이터가 담긴 DTO
	 * @param pet 예약에 연결된 Pet 엔티티
	 * @param vet 예약에 연결된 Vet 엔티티
	 * @return 생성된 Appointment 엔티티
	 * */
	public static Appointment createEntityFromDto(AppointmentRequestDto dto, Pet pet, Vet vet) {
		return Appointment.builder()
			.apptDateTime(dto.getApptDateTime())
			.status(dto.getStatus())
			.symptoms(dto.getSymptoms())
			.petId(pet)
			.vetId(vet)
			.build();
	}

	/**
	 * 예약, Pet, Vet 엔티티를 기반으로 응답 DTO를 생성하는 내부 메서드
	 *
	 * @param appointment 	변환할 예약 엔티티
	 * @param pet			예약에 연결된 Pet 엔티티
	 * @param vet			예약에 연결된 Vet 엔티티
	 * @return 변환된 AppointmentResponseDto
	 */
	private static AppointmentResponseDto mapToResponseDto(Appointment appointment, Pet pet, Vet vet) {
		return AppointmentResponseDto.builder()
			.id(appointment.getId())
			.apptDateTime(appointment.getApptDateTime())
			.status(appointment.getStatus())
			.symptoms(appointment.getSymptoms())
			.petName(pet.getName())
			.vetName(vet.getName())
			.build();
	}
}
