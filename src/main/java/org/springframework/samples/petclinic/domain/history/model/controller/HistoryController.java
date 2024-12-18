package org.springframework.samples.petclinic.domain.history.model.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.appointment.dto.ResultResponseDto;
import org.springframework.samples.petclinic.domain.history.model.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.model.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.model.service.HistoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;

//	@PostMapping
//	public ResponseEntity<?> addHistory(@RequestBody HistoryRequestDto request) {
//		HistoryResponseDto response = historyService.addHistory(request);
//		return new ResponseEntity<>(response, HttpStatus.CREATED);
//	}
}
