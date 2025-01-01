package domain.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.domain.speciality.dto.SpecialityRequestDto;
import org.springframework.samples.petclinic.domain.vet.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.controller.VetController;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.service.VetService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VetControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	private VetController vetController;

	@Mock
	private VetService vetService;

	private ObjectMapper objectMapper = new ObjectMapper();

	private Vet vet;
	private Specialty specialty;
	private Specialty specialty2;
	private VetRequestDto vetRequestDto;
	private VetRequestDto vetUpdateRequestDto;
	private List<SpecialityRequestDto> specialityRequestDto;
	private List<SpecialityRequestDto> specialityUpdateRequestDto;
	private VetResponseDto expectedVetResponseDto;
	private VetResponseDto expectedVetUpdateResponseDto;

	@BeforeEach
	@DisplayName("Sample Data")
	void setUp() {
		MockitoAnnotations.openMocks(this);
		vetController = new VetController(vetService); // 명시적으로 Mock 주입
		mockMvc = MockMvcBuilders.standaloneSetup(vetController).build();

		sampleVet();
		sampleSpecialityRequestDto();
		sampleSpecialityUpdateRequestDto();
		sampleVetRequestDto();
		sampleVetUpdateRequestDto();
		sampleSpeciality();
		sampleSpeciality2();
		sampleExpectedVetResponse();
		sampleExpectedVetUpdateResponse();
	}

	void sampleVet() {
		vet = Vet.builder()
			.id(1)
			.name("테스트용")
			.build();
	}

	void sampleVetRequestDto() {
		vetRequestDto = new VetRequestDto();
		vetRequestDto.setName("테스트용");
		vetRequestDto.setSpecialties(specialityRequestDto);
	}

	void sampleVetUpdateRequestDto() {
		vetUpdateRequestDto = new VetRequestDto();
		vetUpdateRequestDto.setName("업데이트");
		vetUpdateRequestDto.setSpecialties(specialityUpdateRequestDto);
	}

	void sampleSpecialityRequestDto() {
		specialityRequestDto = new ArrayList<>();
		SpecialityRequestDto speciality = new SpecialityRequestDto();
		speciality.setName("외과");
		specialityRequestDto.add(speciality);
	}

	void sampleSpecialityUpdateRequestDto() {
		specialityUpdateRequestDto = new ArrayList<>();
		SpecialityRequestDto speciality = new SpecialityRequestDto();
		speciality.setName("소아과");
		specialityUpdateRequestDto.add(speciality);
	}

	void sampleSpeciality() {
		specialty = new Specialty();
		specialty.setId(1);
		specialty.setName("외과");
	}


	void sampleExpectedVetResponse() {
		expectedVetResponseDto = new VetResponseDto(vet.getId(), vet.getName(), null, null, List.of(specialty));
	}

	void sampleExpectedVetUpdateResponse() {
		expectedVetUpdateResponseDto = new VetResponseDto(vet.getId(), vetUpdateRequestDto.getName(), null, null, List.of(specialty2));
	}

	void sampleSpeciality2() {
		specialty2 = new Specialty();
		specialty2.setId(2);
		specialty2.setName("소아과");
	}

	@Test
	@DisplayName("수의사 등록 성공")
	void createVet_Success_WithToken() throws Exception {
		when(vetService.register(any(VetRequestDto.class))).thenReturn(expectedVetResponseDto);

		String requestJson = objectMapper.writeValueAsString(vetRequestDto);

		MvcResult mvcResult = mockMvc.perform(post("/vets")
				.header("Authorization", "abcd")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isOk())
			.andReturn();

		verify(vetService, times(1)).register(any(VetRequestDto.class));
	}


	@Test
	@DisplayName("전체 수의사 조회 성공")
	void getAllVet_Success_WithToken() throws Exception {
		when(vetService.findAll()).thenReturn(List.of(expectedVetResponseDto));

		mockMvc.perform(get("/vets/all")
				.header("Authorization", "abcd")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("테스트용"))
			.andExpect(jsonPath("$[0].specialties[0].id").value(1))
			.andExpect(jsonPath("$[0].specialties[0].name").value("외과"))
			.andDo(print());

		verify(vetService, times(1)).findAll();
	}

	@Test
	@DisplayName("특정 수의사 조회 성공")
	void getVet_Success_WithToken() throws Exception {
		when(vetService.findById(1)).thenReturn(expectedVetResponseDto);

		mockMvc.perform(get("/vets/1")
				.header("Authorization", "abcd")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("테스트용"))
			.andExpect(jsonPath("$.specialties[0].id").value(1))
			.andExpect(jsonPath("$.specialties[0].name").value("외과"))
			.andDo(print());

		verify(vetService, times(1)).findById(1);
	}

	@Test
	@DisplayName("분야별 수의사 조회 성공")
	void getVetsBySpecialityId_Success_WithToken() throws Exception {
		when(vetService.findBySpecialtyId(specialty.getId())).thenReturn(List.of(expectedVetResponseDto));

		mockMvc.perform(get("/vets")
				.header("Authorization", "abcd")
				.param("speciality", String.valueOf(specialty.getId()))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("테스트용"))
			.andExpect(jsonPath("$[0].specialties[0].id").value(1))
			.andExpect(jsonPath("$[0].specialties[0].name").value("외과"))
			.andDo(print());

		verify(vetService, times(1)).findBySpecialtyId(specialty.getId());
	}

	@Test
	@DisplayName("수의사 수정 성공")
	void updateVet_Success_WithToken() throws Exception {
		when(vetService.update(eq(vet.getId()), any(VetRequestDto.class))).thenReturn(expectedVetUpdateResponseDto);

		String requestJson = objectMapper.writeValueAsString(vetUpdateRequestDto);

		mockMvc.perform(put("/vets/{vet-id}", vet.getId())
				.header("Authorization", "abcd")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("업데이트"))
			.andExpect(jsonPath("$.specialties[0].id").value(2))
			.andExpect(jsonPath("$.specialties[0].name").value("소아과"))
			.andDo(print());

		verify(vetService, times(1)).update(eq(vet.getId()), any(VetRequestDto.class));
	}


	@Test
	@DisplayName("수의사 삭제 성공")
	void deleteVet_Success_WithToken() throws Exception {
		doNothing().when(vetService).delete(1);

		mockMvc.perform(delete("/vets/1")
				.header("Authorization", "abcd")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent())
			.andDo(print());

		verify(vetService, times(1)).delete(1);
	}

}
