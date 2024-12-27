package org.springframework.samples.petclinic.domain.appointment.mapper;

import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

public class AppointmentHelper {

	// DTO와 연관 엔티티를 사용해 예약 엔티티를 업데이트
	public static void updateAppointmentFromDto(AppointmentRequestDto dto, Appointment appointment, Pet pet, Vet vet) {
		appointment.updateAppointment(
			dto.getApptDateTime(),
			dto.getStatus(),
			dto.getSymptoms(),
			pet,
			vet
		);
	}

	// 예약 엔티티를 응답 DTO로 변환
	public static AppointmentResponseDto convertToResponse(Appointment appointment) {
		return mapToResponseDto(appointment, appointment.getPetId(), appointment.getVetId());
	}

	// 예약 및 엔티티를 사용해 응답 DTO 생성
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

	// DTO와 연관 엔티티를 사용해 예약 엔티티 생성
	public static Appointment createEntityFromDto(AppointmentRequestDto dto, Pet pet, Vet vet) {
		return Appointment.builder()
			.apptDateTime(dto.getApptDateTime())
			.status(dto.getStatus())
			.symptoms(dto.getSymptoms())
			.petId(pet)
			.vetId(vet)
			.build();
	}

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
