package org.springframework.samples.petclinic.domain.appointment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentRequestDto;
import org.springframework.samples.petclinic.domain.appointment.dto.AppointmentResponseDto;
import org.springframework.samples.petclinic.domain.appointment.model.Appointment;
import org.springframework.samples.petclinic.domain.appointment.service.CreateAppointmentService;
import org.springframework.samples.petclinic.domain.appointment.service.DeleteAppointmentService;
import org.springframework.samples.petclinic.domain.appointment.service.ReadAppointmentService;
import org.springframework.samples.petclinic.domain.appointment.service.UpdateAppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointment")
public class AppointmentController {

	private final CreateAppointmentService createAppointmentService;
	private final ReadAppointmentService readAppointmentService;
	private final UpdateAppointmentService updateAppointmentService;
	private final DeleteAppointmentService deleteAppointmentService;

	// 예약 생성
	@PostMapping
	public ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody AppointmentRequestDto request) {
		Appointment appointment = createAppointmentService.createAppointment(request);
		AppointmentResponseDto response = new AppointmentResponseDto(appointment);
		return ResponseEntity.ok(response);
	}

	// 전체 예약 조회
	@GetMapping
	public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
		return ResponseEntity.ok(readAppointmentService.findAllAppointments());
	}

	// 특정 예약 조회
	@GetMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable("appointmentId") Integer appointmentId) {
		AppointmentResponseDto response = readAppointmentService.findAppointment(appointmentId);
		return ResponseEntity.ok(response);
	}

	// 예약 수정
	@PutMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable("appointmentId") Integer appointmentId,
																	@RequestBody AppointmentRequestDto request) {
		AppointmentResponseDto response = updateAppointmentService.updateAppointment(appointmentId, request);
		return ResponseEntity.ok(response);
	}

	// 예약 삭제
	@DeleteMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> deleteAppointment(@PathVariable("appointmentId") Integer appointmentId) {
		deleteAppointmentService.deleteAppointment(appointmentId);
		return ResponseEntity.ok().build();
	}
}
