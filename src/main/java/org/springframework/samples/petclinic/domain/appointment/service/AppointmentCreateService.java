package org.springframework.samples.petclinic.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.dto.Result;
import org.springframework.samples.petclinic.domain.appointment.dto.StatusCode;
import org.springframework.samples.petclinic.domain.appointment.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.exception.VetNotFoundException;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbagePetRepository;
import org.springframework.samples.petclinic.domain.appointment.garbage.GarbageVetRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예약 생성 서비스를 제공하는 클래스입니다.
 * <p>
 * 이 클래스는 예약 생성에 필요한 비즈니스 로직을 처리하며,
 * 데이터베이스와의 상호작용을 통해 예약 데이터를 저장합니다.
 */
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
     */
	public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
		Pet pet = getPetByIdOrThrow(dto);
		Vet vet = getVetByIdOrThrow(dto);

		Appointment appointment = createAppointmentEntity(dto, pet, vet);
		Appointment savedAppointment = getSavedAppointment(appointment);

		return createAppointmentResponse(savedAppointment, pet, vet);
	}

	/**
	 * 데이터베이스에서 Pet 엔티티를 조회하거나, 존재하지 않을 경우 예외를 던집니다.
	 *
	 * @param dto 요청 DTO
	 * @return 조회된 Pet 엔티티
	 * @throws PetNotFoundException petId가 유효하지 않을 경우 발생
     */
	private Pet getPetByIdOrThrow(AppointmentRequestDto dto) {
        return petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));
	}

	/**
	 * 데이터베이스에서 Vet 엔티티를 조회하거나, 존재하지 않을 경우 예외를 던집니다.
	 *
	 * @param dto 요청 DTO
	 * @return 조회된 Vet 엔티티
	 * @throws VetNotFoundException vetId가 유효하지 않을 경우 발생
     */
	private Vet getVetByIdOrThrow(AppointmentRequestDto dto) {
        return vetRepository.findById(dto.getVetId())
                .orElseThrow(() -> new VetNotFoundException("Vet not found"));
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
        return Appointment.builder()
            .apptDate(dto.getApptDate())
            .status(dto.getStatus())
            .symptoms(dto.getSymptoms())
            .petId(pet)
            .vetId(vet)
            .build();
	}

	/**
	 * Appointment 엔티티를 데이터베이스에 저장하고 저장된 결과를 반환합니다.
	 *
	 * @param appointment 생성된 Appointment 엔티티
	 * @return 저장된 Appointment 엔티티
	 */
	private Appointment getSavedAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
	}


	/**
	 * 저장된 Appointment 데이터를 바탕으로 AppointmentResponseDto를 생성합니다.
	 *
	 * @param savedAppointment 저장된 Appointment 엔티티
	 * @param pet 예약과 연결된 Pet 엔티티
	 * @param vet 예약과 연결된 Vet 엔티티
	 * @return AppointmentResponseDto 객체
	 */
	private static AppointmentResponseDto createAppointmentResponse(Appointment savedAppointment, Pet pet, Vet vet) {
		Result result = buildSuccessResult();
		AppointmentResponseDto.Body body = buildAppointmentBody(savedAppointment, pet, vet);
		return buildAppointmentResponse(result, body);
	}

	/**
	 * 성공 상태의 Result 객체를 생성합니다.
	 *
	 * @return 성공 상태를 나타내는 Result 객체
	 */
	private static Result buildSuccessResult() {
		return Result.builder()
				.resultCode(StatusCode.SUCCESS.getCode())
				.resultDescription(StatusCode.SUCCESS.getDescription())
				.build();
	}

	/**
	 * 저장된 Appointment 데이터를 기반으로 Body 객체를 생성합니다.
	 *
	 * @param savedAppointment 저장된 Appointment 엔티티
	 * @param pet 예약과 연결된 Pet 엔티티
	 * @param vet 예약과 연결된 Vet 엔티티
	 * @return 생성된 Body 객체
	 */
	private static AppointmentResponseDto.Body buildAppointmentBody(Appointment savedAppointment, Pet pet, Vet vet) {
		return AppointmentResponseDto.Body.builder()
				.id(savedAppointment.getId())
				.petName(pet.getName())
				.vetName(vet.getName())
				.apptDate(savedAppointment.getApptDate())
				.status(savedAppointment.getStatus())
				.symptoms(savedAppointment.getSymptoms())
				.build();
	}

	/**
	 * Result와 Body 객체를 조합하여 최종 ResponseDto를 생성합니다.
	 *
	 * @param result API 응답의 상태 정보를 담은 Result 객체
	 * @param body 예약 정보를 담은 Body 객체
	 * @return 조립된 AppointmentResponseDto 객체
	 */
	private static AppointmentResponseDto buildAppointmentResponse(Result result, AppointmentResponseDto.Body body) {
		return AppointmentResponseDto.builder()
				.result(result)
				.body(body)
				.build();
	}
}
