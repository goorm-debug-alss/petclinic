package org.springframework.samples.petclinic.domain.appointment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentCreateService;
import org.springframework.web.bind.annotation.*;


/**
 * 예약 관련 요청을 처리하는 컨트롤러 클래스입니다.
 * 예약 생성 요청을 처리하며, 관련된 서비스 계층과 연결됩니다.
 */
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentCreateService appointmentCreateService;

	/**
	 * 새로운 예약을 생성하는 API 엔드포인트입니다.
	 * @param requestDto 클라이언트로부터 전달받은 예약 생성 요청 데이터
	 * @return 생성된 예약 정보를 포함하는 응답 DTO를 반환
	 * @throws IllegalAccessException 유효하지 않은 petId 또는 vetId가 전달된 경우 발생
	 */
	@PostMapping
	public ResponseEntity<AppointmentResponseDto> createAppointment(@RequestBody AppointmentRequestDto requestDto) throws IllegalAccessException {
		AppointmentResponseDto responseDto = appointmentCreateService.createAppointment(requestDto);
		return ResponseEntity.ok(responseDto);
	}
}
