package org.springframework.samples.petclinic.domain.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
	boolean existsByPetAndVetAndApptDateTime(Pet pet, Vet vet, LocalDateTime apptDateTime);
}
