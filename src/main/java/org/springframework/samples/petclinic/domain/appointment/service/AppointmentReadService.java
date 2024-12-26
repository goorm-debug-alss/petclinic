package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentHelper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 조회 서비스
 * - 예약 정보를 조회하고 응답 객체를 반환
 * */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentReadService {

	private final AppointmentRepository appointmentRepository;
	private final EntityRetrievalService entityFetchService;

	/**
	 * 모든 예약 정보를 조회
	 *
	 * @return 예약 리스트를 기반으로 생성된 AppointmentResponseDto 리스트
	 */
	public List<AppointmentResponseDto> findAllAppointments() {
		return entityFetchService.fetchAllAppointments().stream()
			.map(AppointmentHelper::convertToResponse)
			.collect(Collectors.toList());
	}

	/**
	 * ID로 예약 정보를 조회
	 *
	 * @param id 조회할 예약의 ID
	 * @return ID에 해당하는 AppointmentResponseDto
	 */
	public AppointmentResponseDto findAppointmentById(Integer id) {
		Appointment appointment = entityFetchService.fetchAppointmentByIdOrThrow(id);
		Pet pet = appointment.getPetId();
		Vet vet = appointment.getVetId();
		return AppointmentHelper.createResponseDto(appointment, pet, vet);
	}
}
