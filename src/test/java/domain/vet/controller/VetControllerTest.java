package domain.vet.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.domain.token.helper.JwtTokenHelper;
import org.springframework.samples.petclinic.domain.vet.repository.SpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.service.VetService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = PetClinicApplication.class)
@AutoConfigureMockMvc
@Transactional
public class VetControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private VetService vetService;

	@Autowired
	private SpecialityRepository specialityRepository;

	private VetRequestDto vetRequestDto;

	private String token;
	@Autowired
	private VetRepository vetRepository;

	@BeforeEach
	void setUp() {
		sampleVetRequestDto();
		vetRepository.deleteAll();
		token = generateTestToken();
	}

	void sampleVetRequestDto() {
		Specialty specialty1 = new Specialty();
		specialty1.setName("외과");
		specialityRepository.save(specialty1);

		Specialty specialty2 = new Specialty();
		specialty2.setName("치과");
		specialityRepository.save(specialty2);

		vetRequestDto = new VetRequestDto();
		vetRequestDto.setName("테스트");
		vetRequestDto.setSpecialties(List.of(specialty1.getId(), specialty2.getId()));
	}

	@Test
	@DisplayName("POST /vets - 수의사 등록 성공")
	void registerVet_shouldSucceed() throws Exception {
		// when
		MockHttpServletResponse response = performRequest("/vets", vetRequestDto, HttpMethod.POST);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		VetResponseDto responseDto = objectMapper.readValue(response.getContentAsString(), VetResponseDto.class);
		assertThat(responseDto.getName()).isEqualTo("테스트");
		assertThat(responseDto.getSpecialties()).hasSize(2);
	}

	@Test
	@DisplayName("GET /vets - 모든 수의사 조회 성공")
	void getAllVets_shouldReturnAllVets() throws Exception {
		// given
		performRequest("/vets", vetRequestDto, HttpMethod.POST);

		// when
		MockHttpServletResponse response = performRequest("/vets/all", HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		VetResponseDto[] vets = objectMapper.readValue(response.getContentAsString(), VetResponseDto[].class);
		assertThat(vets).hasSize(1);
		assertThat(vets[0].getName()).isEqualTo("테스트");
	}

	@Test
	@DisplayName("GET /vets/{vetId} - 특정 수의사 조회 성공")
	void getVetById_shouldReturnVetDetails() throws Exception {
		// given
		VetResponseDto savedVet = vetService.register(vetRequestDto);

		// when
		MockHttpServletResponse response = performRequest("/vets/" + savedVet.getId(), HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		VetResponseDto responseDto = objectMapper.readValue(response.getContentAsString(), VetResponseDto.class);
		assertThat(responseDto.getName()).isEqualTo("테스트");
		assertThat(responseDto.getSpecialties()).hasSize(2);
	}

	@Test
	@DisplayName("GET /vets?speciality= - 특정 전문분야 수의사 조회 성공")
	void getVetBySpecialtyId_shouldReturnVetDetails() throws Exception {
		// given
		VetResponseDto savedVet = vetService.register(vetRequestDto);

		// when
		MockHttpServletResponse response = performRequest("/vets?speciality=" + savedVet.getSpecialties().get(0).getId(), HttpMethod.GET);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		List<VetResponseDto> responseDto = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});
		assertThat(responseDto.get(0).getName()).isEqualTo("테스트");
		assertThat(responseDto.get(0).getSpecialties()).hasSize(2);
	}

	@Test
	@DisplayName("PUT /vets/{vetId} - 수의사 정보 수정 성공")
	void updateVet_shouldSucceed() throws Exception {
		// given
		VetResponseDto savedVet = vetService.register(vetRequestDto);

		VetRequestDto updateRequest = new VetRequestDto();
		updateRequest.setName("수정수의사");
		updateRequest.setSpecialties(List.of(1));

		// when
		MockHttpServletResponse response = performRequest("/vets/" + savedVet.getId(), updateRequest, HttpMethod.PUT);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		VetResponseDto responseDto = objectMapper.readValue(response.getContentAsString(), VetResponseDto.class);
		assertThat(responseDto.getName()).isEqualTo("수정수의사");
		assertThat(responseDto.getSpecialties()).hasSize(1);
	}

	@Test
	@DisplayName("DELETE /vets/{vetId} - 수의사 삭제 성공")
	void deleteVet_shouldSucceed() throws Exception {
		// given
		VetResponseDto savedVet = vetService.register(vetRequestDto);

		// when
		MockHttpServletResponse response = performRequest("/vets/" + savedVet.getId(), HttpMethod.DELETE);

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(vetService.findAll()).isEmpty();
	}

	private String generateTestToken() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("ownerId", 1);
		claims.put("role", "ROLE_USER");
		return jwtTokenHelper.issueAccessToken(claims).getToken();
	}

	private MockHttpServletResponse performRequest(String url, Object request, HttpMethod method) throws Exception {
		return mockMvc.perform(
			request(HttpMethod.valueOf(method.name()), url)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
				.content(objectMapper.writeValueAsString(request))
		).andReturn().getResponse();
	}

	private MockHttpServletResponse performRequest(String url, HttpMethod method) throws Exception {
		return mockMvc.perform(
			request(HttpMethod.valueOf(method.name()), url)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
		).andReturn().getResponse();
	}
}
