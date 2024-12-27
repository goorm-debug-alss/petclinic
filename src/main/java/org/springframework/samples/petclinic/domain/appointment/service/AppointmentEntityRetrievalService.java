package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentEntityRetrievalService {

	private final AppointmentRepository appointmentRepository;
	private final PetRepository petRepository;
	private final VetRepository vetRepository;

	@Transactional(readOnly = true)
	public List<Appointment> fetchAllAppointments() {
		return appointmentRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Appointment fetchAppointmentByIdOrThrow(Integer appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
	}

	@Transactional(readOnly = true)
	public Pet fetchPetByIdOrThrow(Integer petId) {
		return petRepository.findById(petId)
			.orElseThrow(() -> new PetNotFoundException(petId));
	}

	@Transactional(readOnly = true)
	public Vet fetchVetByIdOrThrow(Integer vetId) {
		return vetRepository.findById(vetId)
			.orElseThrow(() -> new VetNotFoundException(vetId));
	}
}
