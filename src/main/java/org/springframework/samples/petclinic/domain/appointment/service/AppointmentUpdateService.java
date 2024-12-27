package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentHelper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentUpdateService {

	private final AppointmentRepository appointmentRepository;
	private final AppointmentEntityRetrievalService entityRetrievalService;

	// 예약 정보 업데이트
	public AppointmentResponseDto updateAppointment(Integer appointmentId, AppointmentRequestDto dto) {
		Appointment appointment = fetchAppointmentByIdOrThrow(appointmentId);
		Pet pet = fetchPetByIdOrThrow(dto);
		Vet vet = fetchVetByIdOrThrow(dto);

		AppointmentHelper.updateAppointmentFromDto(dto, appointment, pet, vet);

		return saveAndConvertToResponse(appointment);
	}

	private Vet fetchVetByIdOrThrow(AppointmentRequestDto dto) {
		return entityRetrievalService.fetchVetByIdOrThrow(dto.getVetId());
	}

	private Pet fetchPetByIdOrThrow(AppointmentRequestDto dto) {
		return entityRetrievalService.fetchPetByIdOrThrow(dto.getPetId());
	}

	private Appointment fetchAppointmentByIdOrThrow(Integer appointmentId) {
		return entityRetrievalService.fetchAppointmentByIdOrThrow(appointmentId);
	}

	private AppointmentResponseDto saveAndConvertToResponse(Appointment appointment) {
		Appointment updatedAppointment = appointmentRepository.save(appointment);

		return AppointmentHelper.convertToResponse(updatedAppointment);
	}
}
