package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.Result;
import org.springframework.samples.petclinic.domain.appointment.dto.ResultResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.StatusCode;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
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

	/**
	 * 모든 예약 정보를 조회
	 *
	 * @return 예약 목록과 결과 정보를 포함한 {@link ResultResponseDto}
	 */
	public ResultResponseDto<AppointmentResponseDto.Body> findALlAppointments() {
		List<AppointmentResponseDto.Body> bodyList = mapAllAppointmentsToBody();
		return createResultResponse(bodyList);
	}

	/**
	 * 예약 ID로 예약 정보를 조회
	 *
	 * @param appointmentId 조회할 예약의 ID
	 * @return {@link AppointmentResponseDto.Body} 예약 정보 DTO
	 * @throws IllegalArgumentException 유효하지 않은 예약 ID가 전달될 경우 발생
	 */
	public AppointmentResponseDto.Body findAppointmentById(Integer appointmentId) {
		Appointment appointment = fetchAppointmentById(appointmentId);
		Pet pet = appointment.getPetId();
		Vet vet = appointment.getVetId();
		return AppointmentHelper.toBody(appointment, pet, vet);
	}

	private List<AppointmentResponseDto.Body> mapAllAppointmentsToBody() {
		return appointmentRepository.findAll().stream()
			.map(this::convertAppointmentToBody)
			.collect(Collectors.toList());
	}

	private AppointmentResponseDto.Body convertAppointmentToBody(Appointment appointment) {
		Pet pet = appointment.getPetId();
		Vet vet = appointment.getVetId();
		return AppointmentHelper.toBody(appointment, pet, vet);
	}

	private ResultResponseDto<AppointmentResponseDto.Body> createResultResponse(List<AppointmentResponseDto.Body> bodyList) {
		Result result = Result.builder()
			.resultCode(StatusCode.SUCCESS.getCode())
			.resultDescription(StatusCode.SUCCESS.getDescription())
			.build();

		return ResultResponseDto.<AppointmentResponseDto.Body>builder()
			.result(result)
			.body(bodyList)
			.build();
	}

	private Appointment fetchAppointmentById(Integer appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
	}
}
