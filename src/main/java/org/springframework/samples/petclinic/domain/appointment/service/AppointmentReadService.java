package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentHelper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentReadService {

	private final AppointmentEntityRetrievalService entityRetrievalService;

	// 모든 예약 정보 조회
	public List<AppointmentResponseDto> findAllAppointments() {
		return entityRetrievalService.fetchAllAppointments().stream()
			.map(AppointmentHelper::convertToResponse)
			.collect(Collectors.toList());
	}

	// 특정 ID로 예약 정보 조회
	public AppointmentResponseDto findAppointmentById(Integer id) {
		Appointment appointment = fetchAppointmentByIdOrThrow(id);
		Pet pet = appointment.getPetId();
		Vet vet = appointment.getVetId();
		return buildAppointmentResponse(appointment, pet, vet);
	}

	private static AppointmentResponseDto buildAppointmentResponse(Appointment appointment, Pet pet, Vet vet) {
		return AppointmentHelper.createResponseDto(appointment, pet, vet);
	}

	private Appointment fetchAppointmentByIdOrThrow(Integer id) {
		return entityRetrievalService.fetchAppointmentByIdOrThrow(id);
	}
}
