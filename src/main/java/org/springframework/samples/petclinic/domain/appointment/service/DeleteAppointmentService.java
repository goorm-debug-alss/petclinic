package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.common.error.AppointmentErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAppointmentService {

	private final AppointmentRepository appointmentRepository;

	public void deleteAppointment(Integer appointmentId) {
		Appointment appointment = getAppointmentOrThrow(appointmentId);
		appointmentRepository.delete(appointment);
	}

	private Appointment getAppointmentOrThrow(Integer appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new ApiException(AppointmentErrorCode.NO_APPOINTMENT));
	}
}
