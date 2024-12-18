package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.Result;
import org.springframework.samples.petclinic.domain.appointment.dto.ResultResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.StatusCode;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 조회와 관련된 서비스 클래스입니다.
 * <p>
 * 예약 정보를 데이터베이스에서 조회하여 반환합니다.
 * */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentReadService {

	private final AppointmentRepository appointmentRepository;

	/**
	 * 모든 예약 정보를 조회하여 반환합니다.
	 *
	 * @return {@link ResultResponseDto} 형태의 결과와 예약 정보 목록
	 */
	public ResultResponseDto<AppointmentResponseDto.Body> getAllAppointments() {
		List<AppointmentResponseDto.Body> bodyList = getAppointmentBodyList();
		return buildResultResponseDto(bodyList);
	}

	/**
	 * 데이터베이스에서 모든 예약 정보를 조회하고 DTO 형태로 변환합니다.
	 *
	 * @return {@link List} 형태의 예약 정보 DTO 목록
	 */
	private List<AppointmentResponseDto.Body> getAppointmentBodyList() {
		return appointmentRepository.findAll().stream()
			.map(appointment -> AppointmentResponseDto.Body.builder()
				.id(appointment.getId())
				.apptDate(appointment.getApptDate())
				.status(appointment.getStatus())
				.symptoms(appointment.getSymptoms())
				.petName(appointment.getPetId().getName())
				.vetName(appointment.getVetId().getName())
				.build())
			.collect(Collectors.toList());
	}

	/**
	 * 결과 코드와 함께 예약 정보 목록을 포함하는 응답 객체를 생성합니다.
	 *
	 * @param bodyList 예약 정보 DTO 목록
	 * @return {@link ResultResponseDto} 형태의 결과와 예약 정보
	 */
	private static ResultResponseDto<AppointmentResponseDto.Body> buildResultResponseDto(List<AppointmentResponseDto.Body> bodyList) {
		Result result = Result.builder()
			.resultCode(StatusCode.SUCCESS.getCode())
			.resultDescription(StatusCode.SUCCESS.getDescription())
			.build();

		return ResultResponseDto.<AppointmentResponseDto.Body>builder()
			.result(result)
			.body(bodyList)
			.build();
	}

	/**
	 * 특정 예약 ID로 예약 정보를 조회하는 메서드입니다.
	 *
	 * @param appointmentId 조회할 예약의 ID
	 * @return {@link AppointmentResponseDto.Body} 예약 정보 DTO
	 * @throws IllegalArgumentException 유효하지 않은 예약 ID가 전달될 경우 발생
	 */
	public AppointmentResponseDto.Body getAppointmentById(Integer appointmentId) {
		Appointment appointment = findAppointmentById(appointmentId);
		return convertToBody(appointment);
	}

	/**
	 * 데이터베이스에서 특정 예약 ID에 해당하는 예약 엔티티를 조회합니다.
	 *
	 * @param appointmentId 조회할 예약의 ID
	 * @return {@link Appointment} 예약 엔티티
	 * @throws IllegalArgumentException 유효하지 않은 예약 ID가 전달될 경우 발생
	 */
	private Appointment findAppointmentById(Integer appointmentId) {
		return appointmentRepository.findById((appointmentId))
			.orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
	}

	/**
	 * 예약 엔티티를 {@link AppointmentResponseDto.Body} DTO로 변환합니다.
	 *
	 * @param appointment 변환할 예약 엔티티
	 * @return {@link AppointmentResponseDto.Body} 예약 정보 DTO
	 */
	private static AppointmentResponseDto.Body convertToBody(Appointment appointment) {
		return AppointmentResponseDto.Body.builder()
			.id(appointment.getId())
			.apptDate(appointment.getApptDate())
			.status(appointment.getStatus())
			.symptoms(appointment.getSymptoms())
			.petName(appointment.getPetId().getName())
			.vetName(appointment.getVetId().getName())
			.build();
	}
}
