package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentDeleteService {

	private final AppointmentRepository appointmentRepository;
	private final AppointmentEntityRetrievalService entityRetrievalService;

	// 특정 ID를 기반으로 예약 삭제
	public void deleteAppointment(Integer appointmentId) {
		Appointment appointment = fetchAppointmentByIdOrThrow(appointmentId);
		appointmentRepository.delete(appointment);
	}

	private Appointment fetchAppointmentByIdOrThrow(Integer appointmentId) {
		return entityRetrievalService.fetchAppointmentByIdOrThrow(appointmentId);
	}
}
