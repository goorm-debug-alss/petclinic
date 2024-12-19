package org.springframework.samples.petclinic.domain.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
}
