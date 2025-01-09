package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.AppointmentErrorCode;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentMapper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateAppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final VetRepository vetRepository;
	private final PetRepository petRepository;
	private final AppointmentMapper appointmentMapper;

	public Appointment createAppointment(AppointmentRequestDto request) {
		validateRequestData(request);

		Vet vet = getVetOrThrow(request);
		Pet pet = getPetOrThrow(request);

		validateDuplicateAppointment(request, pet, vet);

		Appointment appointment = appointmentMapper.toEntity(request, pet, vet);
		return appointmentRepository.save(appointment);
	}

	private void validateRequestData(AppointmentRequestDto request) {
		validateAppointmentDate(request.getApptDateTime());
		validateAppointmentStatus(request.getAppStatus());
		validateSymptoms(request.getSymptoms());
	}

	private void validateAppointmentDate(LocalDateTime apptDateTime) {
		if (apptDateTime == null)
			throw new ApiException(AppointmentErrorCode.NULL_APPOINTMENT_DATE);

		if (apptDateTime.isBefore(LocalDateTime.now()))
			throw new ApiException(AppointmentErrorCode.INVALID_APPOINTMENT_DATE);

		if (!isWithinWorkingHours(apptDateTime))
			throw new ApiException(AppointmentErrorCode.OUTSIDE_WORKING_HOURS);
	}

	private void validateAppointmentStatus(ApptStatus appStatus) {
		if (appStatus == null)
			throw new ApiException(AppointmentErrorCode.NULL_APPOINTMENT_STATUS);
	}

	private void validateSymptoms(String symptoms) {
		if (symptoms == null || symptoms.isEmpty())
			throw new ApiException(AppointmentErrorCode.INVALID_SYMPTOMS);
	}

	private Pet getPetOrThrow(AppointmentRequestDto request) {
		return petRepository.findById(request.getPetId())
			.orElseThrow(() -> new ApiException(PetErrorCode.NO_PET));
	}

	private Vet getVetOrThrow(AppointmentRequestDto request) {
		return vetRepository.findById(request.getVetId())
			.orElseThrow(() -> new ApiException(VetErrorCode.NO_VET));
	}

	private void validateDuplicateAppointment(AppointmentRequestDto request, Pet pet, Vet vet) {
		if (appointmentRepository.existsByPetAndVetAndApptDateTime(pet, vet, request.getApptDateTime()))
			throw new ApiException(AppointmentErrorCode.CONFLICTING_APPOINTMENT);
	}

	private boolean isWithinWorkingHours(LocalDateTime dateTime) {
		int hour = dateTime.getHour();
		return hour >= 9 && hour < 18;
	}
}
