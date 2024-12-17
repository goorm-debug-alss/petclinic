package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.Result;
import org.springframework.samples.petclinic.domain.appointment.dto.StatusCode;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbagePetRepository;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbageVetRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentCreateService {

	private final AppointmentRepository appointmentRepository;
	private final GarbagePetRepository petRepository;
	private final GarbageVetRepository vetRepository;

	/**
	 * 예약 생성 로직을 처리하는 메소드입니다.
	 *
	 * @param dto 예약 생성 요청 데이터를 담고 있는 DTO
	 * @return 생성된 예약 정보를 포함하는 응답 DTO
	 * @throws IllegalAccessException 유효하지 않은 petId 또는 vetId가 전달될 경우 발생
	 */
	public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) throws IllegalAccessException {
		Pet pet = getPetByIdOrThrow(dto);
		Vet vet = getVetByIdOrThrow(dto);
		Appointment appointment = createAppointmentEntity(dto, pet, vet);
		Appointment savedAppointment = getSavedAppointment(appointment);
		return builderResponseDto(savedAppointment, pet, vet);
	}

	/**
	 * 데이터베이스에서 Pet 엔티티를 조회하거나, 존재하지 않을 경우 예외를 던집니다.
	 *
	 * @param dto 요청 DTO
	 * @return 조회된 Pet 엔티티
	 * @throws IllegalAccessException petId가 유효하지 않을 경우 발생
	 */
	private Pet getPetByIdOrThrow(AppointmentRequestDto dto) throws IllegalAccessException {
		Pet pet = petRepository.findById(dto.getPetId())
			.orElseThrow(() -> new IllegalAccessException("Pet not found"));
		return pet;
	}


	/**
	 * 데이터베이스에서 Vet 엔티티를 조회하거나, 존재하지 않을 경우 예외를 던집니다.
	 *
	 * @param dto 요청 DTO
	 * @return 조회된 Vet 엔티티
	 * @throws IllegalAccessException vetId가 유효하지 않을 경우 발생
	 */
	private Vet getVetByIdOrThrow(AppointmentRequestDto dto) throws IllegalAccessException {
		Vet vet = vetRepository.findById(dto.getVetId())
			.orElseThrow(() -> new IllegalAccessException("Vet not found"));
		return vet;
	}


	/**
	 * 요청 데이터를 바탕으로 Appointment 엔티티를 생성합니다.
	 *
	 * @param dto 요청 DTO
	 * @param pet 조회된 Pet 엔티티
	 * @param vet 조회된 Vet 엔티티
	 * @return 생성된 Appointment 엔티티
	 */
	private static Appointment createAppointmentEntity(AppointmentRequestDto dto, Pet pet, Vet vet) {
		Appointment appointment = Appointment.builder()
			.apptDate(dto.getApptDate())
			.status(dto.getStatus())
			.symptoms(dto.getSymptoms())
			.petId(pet)
			.vetId(vet)
			.build();
		return appointment;
	}

	/**
	 * Appointment 엔티티를 데이터베이스에 저장하고 저장된 결과를 반환합니다.
	 *
	 * @param appointment 생성된 Appointment 엔티티
	 * @return 저장된 Appointment 엔티티
	 */
	private Appointment getSavedAppointment(Appointment appointment) {
		Appointment savedAppointment = appointmentRepository.save(appointment);
		return savedAppointment;
	}

	/**
	 * 저장된 Appointment 엔티티와 관련된 정보를 기반으로 응답 DTO를 생성합니다.
	 *
	 * @param savedAppointment 저장된 Appointment 엔티티
	 * @param pet 예약과 연결된 Pet 엔티티 (펫의 이름을 응답에 포함)
	 * @param vet 예약과 연결된 Vet 엔티티 (수의사의 이름을 응답에 포함)
	 * @return 생성된 예약 정보와 성공 결과를 포함하는 응답 DTO
	 *
	 * <p>
	 * 이 메서드는 두 가지 주요 부분으로 응답 DTO를 구성합니다:
	 * - {@link Result}: 요청 처리 결과 코드 및 설명 (예: 성공 상태)
	 * - {@link AppointmentResponseDto.Body}: 예약 ID, 펫 이름, 수의사 이름, 예약 날짜, 상태, 증상 등 예약 세부 정보
	 * </p>
	 */
	private static AppointmentResponseDto builderResponseDto(Appointment savedAppointment, Pet pet, Vet vet) {
		Result result = Result.builder()
			.resultCode(StatusCode.SUCCESS.getCode())
			.resultDescription(StatusCode.SUCCESS.getDescription())
			.build();

		AppointmentResponseDto.Body body = AppointmentResponseDto.Body.builder()
			.id(savedAppointment.getId())
			.petName(pet.getName())
			.vetName(vet.getName())
			.apptDate(savedAppointment.getApptDate())
			.status(savedAppointment.getStatus())
			.symptoms(savedAppointment.getSymptoms())
			.build();

		return AppointmentResponseDto.builder()
			.result(result)
			.body(body)
			.build();
	}
}
