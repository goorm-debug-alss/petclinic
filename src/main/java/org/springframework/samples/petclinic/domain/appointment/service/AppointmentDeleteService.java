package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

/**
 * 예약 삭제 서비스
 * - 예약 ID를 기반으로 데이터를 삭제
 */
@Service
@RequiredArgsConstructor
public class AppointmentDeleteService {

	private final AppointmentRepository appointmentRepository;
	private final EntityRetrievalService entityFetchService;

	/**
	 * 특정 ID를 기반으로 예약 정보를 삭제
	 *
	 * @param appointmentId 삭제할 예약의 ID
	 * @throws AppointmentNotFoundException 예약 ID가 데이터베이스에 없을 경우 발생
	 */
	public void deleteAppointment(Integer appointmentId) {
		Appointment appointment = entityFetchService.fetchAppointmentByIdOrThrow(appointmentId);
		appointmentRepository.delete(appointment);
	}
}
