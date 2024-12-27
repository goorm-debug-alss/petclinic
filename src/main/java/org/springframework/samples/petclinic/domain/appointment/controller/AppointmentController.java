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

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentCreateService appointmentCreateService;
	private final AppointmentReadService appointmentReadService;
	private final AppointmentUpdateService appointmentUpdateService;
	private final AppointmentDeleteService appointmentDeleteService;


	// 예약 생성
	@PostMapping
	public ResponseEntity<AppointmentResponseDto> createAppointment(@RequestBody AppointmentRequestDto requestDto) {
		AppointmentResponseDto responseDto = appointmentCreateService.createAppointment(requestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 모든 예약 조회
	@GetMapping
	public ResponseEntity<List<AppointmentResponseDto>> getALlAppointments() {
		return ResponseEntity.ok(appointmentReadService.findAllAppointments());
	}

	// 특정 예약 조회
	@GetMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> getAppointmentDetails(@PathVariable("appointmentId") Integer appointmentId) {
		AppointmentResponseDto responseDto = appointmentReadService.findAppointmentById(appointmentId);
		return ResponseEntity.ok(responseDto);
	}

	// 예약 수정
	@PutMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable("appointmentId") Integer appointmentId,
																	@RequestBody AppointmentRequestDto requestDto) {
		AppointmentResponseDto responseDto = appointmentUpdateService.updateAppointment(appointmentId, requestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 예약 삭제
	@DeleteMapping("/{appointmentId}")
	public ResponseEntity<Void> deleteAppointment(@PathVariable("appointmentId") Integer appointmentId) {
		appointmentDeleteService.deleteAppointment(appointmentId);
		return ResponseEntity.noContent().build();
	}
}
