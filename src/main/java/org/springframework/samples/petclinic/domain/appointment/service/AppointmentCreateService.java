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

/**
 * 예약 생성 서비스
 * - 예약 생성 로직 및 데이터베이스 연동 처리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentCreateService {

	private final AppointmentRepository appointmentRepository;
	private final EntityRetrievalService entityFetchService;

	/**
	 * 예약 생성
	 *
	 * @param dto 요청 데이터를 기반으로 예약 생성
	 * @return 생성된 AppointmentResponseDto
     */
	public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
		Pet pet = entityFetchService.fetchPetByIdOrThrow(dto.getPetId());
		Vet vet = entityFetchService.fetchVetByIdOrThrow(dto.getVetId());

		Appointment appointment = AppointmentHelper.createEntityFromDto(dto, pet, vet);
		Appointment savedAppointment = appointmentRepository.save(appointment);

		return AppointmentHelper.createResponseDto(savedAppointment, pet, vet);
	}
}
