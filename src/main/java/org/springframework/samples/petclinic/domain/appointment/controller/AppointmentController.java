package org.springframework.samples.petclinic.domain.appointment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentCreateService;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentDeleteService;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentReadService;
import org.springframework.samples.petclinic.domain.appointment.service.AppointmentUpdateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 예약 관련 요청을 처리하는 컨트롤러
 * - 이 클래스는 예약 생성 및 조회 요청을 처리하며, 서비스 계층과 연결되어 비즈니스 로직을 수행
 */
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentCreateService appointmentCreateService;
	private final AppointmentReadService appointmentReadService;
	private final AppointmentUpdateService appointmentUpdateService;
	private final AppointmentDeleteService appointmentDeleteService;

	/**
	 * 새로운 예약을 생성하는 API
	 *
	 * @param requestDto 클라이언트로부터 전달받은 예약 생성 요청 데이터
	 * @return 생성된 예약 정보를 포함하는 응답 DTO (HTTP 상태 코드 200 OK)
     */
	@PostMapping
	public ResponseEntity<AppointmentResponseDto> createAppointment(@RequestBody AppointmentRequestDto requestDto) {
		AppointmentResponseDto responseDto = appointmentCreateService.createAppointment(requestDto);
		return ResponseEntity.ok(responseDto);
	}

	/**
	 * 모든 예약 정보를 조회하는 API
	 *
	 * @return 예약 정보 목록을 포함하는 응답 DTO (HTTP 상태 코드 200 OK)
	 */
	@GetMapping
	public ResponseEntity<List<AppointmentResponseDto>> getALlAppointments() {
		return ResponseEntity.ok(appointmentReadService.findAllAppointments());
	}

	/**
	 * 특정 ID를 기반으로 예약 상세 정보를 조회하는 API
	 *
	 * @param appointmentId 조회할 예약의 ID
	 * @return 예약 상세 정보를 포함하는 응답 DTO (HTTP 상태 코드 200 OK)
     */
	@GetMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> getAppointmentDetails(@PathVariable("appointmentId") Integer appointmentId) {
		AppointmentResponseDto responseDto = appointmentReadService.findAppointmentById(appointmentId);
		return ResponseEntity.ok(responseDto);
	}

	/**
	 * 예약 정보를 업데이트하는 API
	 *
	 * @param appointmentId 업데이트할 예약의 ID
	 * @param requestDto 클라이언트로부터 전달받은 예약 업데이트 요청 데이터
	 * @return 업데이트된 예약 정보를 포함하는 응답 DTO (HTTP 상태 코드 200 OK)
	 * */
	@PutMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable("appointmentId") Integer appointmentId,
																	@RequestBody AppointmentRequestDto requestDto) {
		AppointmentResponseDto responseDto = appointmentUpdateService.updateAppointment(appointmentId, requestDto);
		return ResponseEntity.ok(responseDto);
	}

	/**
	 * 특정 ID를 기반으로 예약 정보를 삭제하는 API
	 *
	 * @param appointmentId 삭제할 예약의 ID
	 * @return 삭제 성공 시 HTTP 상태 코드 204 No Content를 반환
	 */
	@DeleteMapping("/{appointmentId}")
	public ResponseEntity<Void> deleteAppointment(@PathVariable("appointmentId") Integer appointmentId) {
		appointmentDeleteService.deleteAppointment(appointmentId);
		return ResponseEntity.noContent().build();
	}
}
