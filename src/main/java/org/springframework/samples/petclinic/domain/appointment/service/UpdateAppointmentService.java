package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.AppointmentErrorCode;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentMapper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.model.enums.VetStatus;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateAppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PetRepository petRepository;
	private final VetRepository vetRepository;
	private final AppointmentMapper appointmentMapper;

	public AppointmentResponseDto updateAppointment(Integer appointmentId, AppointmentRequestDto request) {
		validateRequestData(request);
		Appointment appointment = getAppointmentOrThrow(appointmentId);
		Pet pet = getPetOrThrow(request);
		Vet vet = getVetOrThrow(request);

		updateAppointmentDetails(request, appointment, pet, vet);

		Appointment updatedAppointment = appointmentRepository.save(appointment);

		return appointmentMapper.toDto(updatedAppointment);
	}

	private Vet getVetOrThrow(AppointmentRequestDto request) {
		return vetRepository.findByIdAndStatus(request.getVetId(), VetStatus.REGISTERED)
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));
	}

	private void validateRequestData(AppointmentRequestDto request) {
		if (request.getApptDateTime().isBefore(LocalDateTime.now()))
			throw new ApiException(AppointmentErrorCode.INVALID_APPOINTMENT_DATE);

		if (request.getSymptoms() == null || request.getSymptoms().isEmpty())
			throw new ApiException(AppointmentErrorCode.INVALID_SYMPTOMS);
	}

	private Pet getPetOrThrow(AppointmentRequestDto request) {
		return petRepository.findById(request.getPetId())
			.orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));
	}

	private Appointment getAppointmentOrThrow(Integer appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new ApiException(AppointmentErrorCode.NO_APPOINTMENT));
	}

	private static void updateAppointmentDetails(AppointmentRequestDto request, Appointment appointment, Pet pet, Vet vet) {
		appointment.updateAppointment(
			request.getApptDateTime(),
			request.getAppStatus(),
			request.getSymptoms(),
			pet,
			vet
		);
	}
}
