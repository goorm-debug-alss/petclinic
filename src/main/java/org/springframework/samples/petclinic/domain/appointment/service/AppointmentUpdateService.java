package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.exception.AppointmentNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.mapper.AppointmentHelper;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 예약 정보 업데이트 서비스
 * - 예약 정보를 수정, 검증, 저장하고 응답
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentUpdateService {

	private final AppointmentRepository appointmentRepository;
	private final PetRepository petRepository;
	private final VetRepository vetRepository;

	/**
	 * 예약 정보를 업데이트하고, 결과를 반환
	 *
	 * @param appointmentId 업데이트할 예약의 ID
	 * @param dto 예약 업데이트 요청 데이터
	 * @return 업데이트된 예약 정보를 포함하는 응답 DTO
	 */
	public AppointmentResponseDto updateAppointment(Integer appointmentId, AppointmentRequestDto dto) {
		Appointment appointment = findAppointmentOrThrow(appointmentId);
		Pet pet = findPetOrThrow(dto);
		Vet vet = findVetOrThrow(dto);

		AppointmentHelper.updateFields(dto, appointment, pet, vet);

		Appointment updatedAppointment = appointmentRepository.save(appointment);

		return AppointmentHelper.buildResponseDto(updatedAppointment);
	}

	private Appointment findAppointmentOrThrow(Integer appointmentId) {
		return appointmentRepository.findById((appointmentId))
			.orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
	}

	private Pet findPetOrThrow(AppointmentRequestDto dto) {
		return petRepository.findById(dto.getPetId())
			.orElseThrow(() -> new PetNotFoundException("Pet not found"));
	}

	private Vet findVetOrThrow(AppointmentRequestDto dto) {
		return vetRepository.findById(dto.getVetId())
			.orElseThrow(() -> new VetNotFoundException("Vet not found"));
	}
}
