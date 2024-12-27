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
public class AppointmentCreateService {

	private final AppointmentRepository appointmentRepository;
	private final AppointmentEntityRetrievalService entityRetrievalService;

	// 예약을 생성하고 저장된 정보를 반환
	public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
		Pet pet = fetchPetByIdOrThrowException(dto);
		Vet vet = fetchVetByIdOrThrowException(dto);

		Appointment appointment = createAppointmentEntity(dto, pet, vet);
		Appointment savedAppointment = appointmentRepository.save(appointment);

		return buildAppointmentResponse(savedAppointment, pet, vet);
	}

	private Pet fetchPetByIdOrThrowException(AppointmentRequestDto dto) {
		return entityRetrievalService.fetchPetByIdOrThrow(dto.getPetId());
	}

	private Vet fetchVetByIdOrThrowException(AppointmentRequestDto dto) {
		return entityRetrievalService.fetchVetByIdOrThrow(dto.getVetId());
	}

	private static Appointment createAppointmentEntity(AppointmentRequestDto dto, Pet pet, Vet vet) {
		return AppointmentHelper.createEntityFromDto(dto, pet, vet);
	}

	private static AppointmentResponseDto buildAppointmentResponse(Appointment savedAppointment, Pet pet, Vet vet) {
		return AppointmentHelper.createResponseDto(savedAppointment, pet, vet);
	}
}
