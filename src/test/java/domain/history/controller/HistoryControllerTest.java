package domain.history.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.domain.history.controller.HistoryController;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.service.HistoryService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

	private MockMvc mockMvc;

	@Mock
	private HistoryService historyService;

	@InjectMocks
	private HistoryController historyController;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(historyController).build();
	}

	@Test
	@DisplayName("POST /history - 새로운 진료 내역 추가 성공")
	void addHistory_Success() throws Exception {
		// Given
		String requestJson = """
			{
			    "symptoms": "감기",
			    "content": "감기약 처방",
			    "vetId": 1,
			    "visitId": 1
			}
			""";

		HistoryResponseDto responseDto = HistoryResponseDto.builder()
			.result(HistoryResponseDto.Result.builder()
				.resultCode("200")
				.resultDescription("Operation completed successfully")
				.build())
			.body(List.of(HistoryResponseDto.Body.builder()
				.historyId(1)
				.symptoms("감기")
				.content("감기약 처방")
				.vetId(1)
				.visitId(1)
				.build()))
			.build();

		when(historyService.addHistory(any(HistoryRequestDto.class))).thenReturn(responseDto);

		// When
		mockMvc.perform(post("/history")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			// Then
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.result.resultCode").value("200"))
			.andExpect(jsonPath("$.result.resultDescription").value("Operation completed successfully"))
			.andExpect(jsonPath("$.body[0].historyId").value(1))
			.andExpect(jsonPath("$.body[0].symptoms").value("감기"))
			.andExpect(jsonPath("$.body[0].content").value("감기약 처방"))
			.andExpect(jsonPath("$.body[0].vetId").value(1))
			.andExpect(jsonPath("$.body[0].visitId").value(1));
	}

	@Test
	@DisplayName("GET /history/{petId} - 특정 반려동물 진료 내역 조회 성공")
	void getHistoriesByPetId_Success() throws Exception {
		// Given
		HistoryResponseDto responseDto = HistoryResponseDto.builder()
			.result(HistoryResponseDto.Result.builder()
				.resultCode("200")
				.resultDescription("Success")
				.build())
			.body(List.of(HistoryResponseDto.Body.builder()
				.historyId(1)
				.symptoms("감기")
				.content("감기약 처방")
				.vetId(1)
				.visitId(1)
				.build()))
			.build();

		when(historyService.getHistoriesByPetId(eq(1))).thenReturn(responseDto);

		// When
		mockMvc.perform(get("/history/1")
				.accept(MediaType.APPLICATION_JSON))
			// Then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.resultCode").value("200"))
			.andExpect(jsonPath("$.result.resultDescription").value("Success"))
			.andExpect(jsonPath("$.body[0].historyId").value(1))
			.andExpect(jsonPath("$.body[0].symptoms").value("감기"))
			.andExpect(jsonPath("$.body[0].content").value("감기약 처방"))
			.andExpect(jsonPath("$.body[0].vetId").value(1))
			.andExpect(jsonPath("$.body[0].visitId").value(1));
	}

	@Test
	@DisplayName("PUT /history/{historyId} - 진료 내역 수정 성공")
	void updateHistory_Success() throws Exception {
		// Given
		String requestJson = """
			{
			    "symptoms": "Updated Symptoms",
			    "content": "Updated Content",
			    "vetId": 1,
			    "visitId": 1
			}
			""";

		HistoryResponseDto responseDto = HistoryResponseDto.builder()
			.result(HistoryResponseDto.Result.builder()
				.resultCode("200")
				.resultDescription("Operation completed successfully")
				.build())
			.body(List.of(HistoryResponseDto.Body.builder()
				.historyId(1)
				.symptoms("Updated Symptoms")
				.content("Updated Content")
				.vetId(1)
				.visitId(1)
				.build()))
			.build();

		when(historyService.updateHistory(eq(1), any(HistoryRequestDto.class))).thenReturn(responseDto);

		// When
		mockMvc.perform(put("/history/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			// Then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.resultCode").value("200"))
			.andExpect(jsonPath("$.result.resultDescription").value("Operation completed successfully"))
			.andExpect(jsonPath("$.body[0].historyId").value(1))
			.andExpect(jsonPath("$.body[0].symptoms").value("Updated Symptoms"))
			.andExpect(jsonPath("$.body[0].content").value("Updated Content"));
	}

	@Test
	@DisplayName("DELETE /history/{historyId} - 진료 내역 삭제 성공")
	void deleteHistory_Success() throws Exception {
		// When
		mockMvc.perform(delete("/history/1"))
			// Then
			.andExpect(status().isOk())
			.andExpect(content().string("History deleted successfully."));
	}
}
