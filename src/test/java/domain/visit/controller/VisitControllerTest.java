//package domain.visit.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.samples.petclinic.domain.visit.controller.VisitController;
//import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
//import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
//import org.springframework.samples.petclinic.domain.visit.service.VisitService;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import java.time.LocalDateTime;
//import java.util.List;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class VisitControllerTest {
//
//	private MockMvc mockMvc;
//
//	@Mock
//	private VisitService visitService;
//
//	@InjectMocks
//	private VisitController visitController;
//
//	@BeforeEach
//	void setup() {
//		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
//	}
//
//	@Test
//	@DisplayName("POST /visits - 새로운 방문 내역 추가 성공")
//	void createVisit_Success() throws Exception {
//
//		// Given
//		String requestJson = """
//        {
//            "petId": 1,
//            "visitDate": "2024-12-18T19:00:00",
//            "description": "진료"
//        }
//        """;
//
//		VisitResponseDto responseDto = VisitResponseDto.builder()
//			.result(VisitResponseDto.Result.builder()
//				.resultCode("200")
//				.resultDescription("Operation completed successfully")
//				.build())
//			.body(List.of(VisitResponseDto.Body.builder()
//				.visitId(1)
//				.petName("PetA")
//				.visitDate(LocalDateTime.of(2024, 12, 18, 19, 0))
//				.description("진료")
//				.build()))
//			.build();
//
//		when(visitService.createVisit(any(VisitRequestDto.class))).thenReturn(responseDto);
//
//		// When
//		mockMvc.perform(post("/visits")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(requestJson))
//			// Then
//			.andExpect(status().isCreated())
//			.andExpect(jsonPath("$.result.resultCode").value("200"))
//			.andExpect(jsonPath("$.result.resultDescription").value("Operation completed successfully"))
//			.andExpect(jsonPath("$.body[0].visitId").value(1))
//			.andExpect(jsonPath("$.body[0].petName").value("PetA"))
//			.andExpect(jsonPath("$.body[0].visitDate").value("2024-12-18T19:00:00"))
//			.andExpect(jsonPath("$.body[0].description").value("진료"));
//	}
//
//	@Test
//	@DisplayName("GET /visits/{petId} - 특정 반려동물 방문 내역 조회 성공")
//	void getVisitsByPetId_Success() throws Exception {
//
//		// Given
//		LocalDateTime visitDate = LocalDateTime.of(2024, 12, 18, 19, 0);
//
//		VisitResponseDto responseDto = VisitResponseDto.builder()
//			.result(VisitResponseDto.Result.builder()
//				.resultCode("200")
//				.resultDescription("Operation completed successfully")
//				.build())
//			.body(List.of(VisitResponseDto.Body.builder()
//				.visitId(1)
//				.petName("PetA")
//				.visitDate(visitDate)
//				.description("진료")
//				.build()))
//			.build();
//
//		when(visitService.getVisitsByPetId(1)).thenReturn(responseDto);
//
//		// When
//		mockMvc.perform(get("/visits/1")
//				.accept(MediaType.APPLICATION_JSON))
//			// Then
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.result.resultCode").value("200"))
//			.andExpect(jsonPath("$.result.resultDescription").value("Operation completed successfully"))
//			.andExpect(jsonPath("$.body[0].visitId").value(1))
//			.andExpect(jsonPath("$.body[0].petName").value("PetA"))
//			.andExpect(jsonPath("$.body[0].visitDate").value("2024-12-18T19:00:00"))
//			.andExpect(jsonPath("$.body[0].description").value("진료"));
//	}
//}
