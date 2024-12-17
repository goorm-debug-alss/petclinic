package org.springframework.samples.petclinic.domain.appointment.exception;

/**
 * appointmentId를 찾을 수 없을 때 발생하는 예외입니다.
 */
public class AppointmentNotFoundException extends RuntimeException {
	public AppointmentNotFoundException(Integer appointmentId) {
		super("Appointment not found with ID: " + appointmentId);
	}
}
