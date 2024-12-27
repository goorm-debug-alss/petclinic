package org.springframework.samples.petclinic.domain.appointment.exception;

public class AppointmentNotFoundException extends RuntimeException {
	public AppointmentNotFoundException(Integer appointmentId) {
		super("Appointment not found with ID: " + appointmentId);
	}
}
