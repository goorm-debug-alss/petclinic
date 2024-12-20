package domain.vet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.domain.speciality.dto.SpecialityRequestDto;
import org.springframework.samples.petclinic.domain.speciality.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.controller.VetController;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.service.VetService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VetControllerTest {

	@InjectMocks
	private VetController vetController;

	@Mock
	private VetService vetService;

	private VetResponseDto expectedVetResponseDto;
	private VetRequestDto expectedVetRequestDtoChangeName;
	private VetRequestDto expectedVetRequestDtoChangeSpecialty;
	private VetRequestDto vetRequestDto;
	private SpecialityRequestDto specialityRequestDto;
	private Specialty specialty;

	@BeforeEach
	void setUp() {
		sampleVetRequestDto();
		sampleSpecialityRequestDto();
		sampleSpeciality();
		sampleExpectedVetResponse();
		sampleExpectedVetResponseChangeName();
		sampleExpectedVetResponseChangeSpecialty();

	}

	void sampleVetRequestDto() {
		vetRequestDto = new VetRequestDto();
		vetRequestDto.setName("DTO테스트용");
		SpecialityRequestDto specialityRequestDto = new SpecialityRequestDto();
		specialityRequestDto.setName("외과");
		vetRequestDto.setSpecialties(List.of(specialityRequestDto));
	}

	void sampleSpeciality() {
		specialty = new Specialty();
		specialty.setId(1);
		specialty.setName("외과");
	}

	void sampleExpectedVetResponse() {
		expectedVetResponseDto = new VetResponseDto(1, "테스트용 수의사", null, null, List.of(specialty));
	}

	void sampleSpecialityRequestDto(){
		specialityRequestDto = new SpecialityRequestDto("외과");
	}

	void sampleExpectedVetResponseChangeName(){
		expectedVetRequestDtoChangeName = new VetRequestDto("이름 변경", List.of(specialityRequestDto));
	}

	void sampleExpectedVetResponseChangeSpecialty(){
		expectedVetRequestDtoChangeSpecialty = new VetRequestDto("테스트용 수의사", List.of(new SpecialityRequestDto("내과")));
	}

	@Test
	@DisplayName("수의사 등록 성공")
	void createVet_Success(){
		when(vetService.register(any(VetRequestDto.class))).thenReturn(expectedVetResponseDto);

		ResponseEntity<VetResponseDto> response = vetController.create(vetRequestDto);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(response.getBody().getName()).isEqualTo(expectedVetResponseDto.getName());

		verify(vetService, times(1)).register(vetRequestDto);
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("수의사 전체 조회 성공")
	void getAllVet_Success(){
		when(vetService.findAll()).thenReturn(List.of(expectedVetResponseDto));

		ResponseEntity<List<VetResponseDto>> response = vetController.getAll();

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().size()).isEqualTo(1);
		assertThat(response.getBody().get(0).getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(response.getBody().get(0).getName()).isEqualTo(expectedVetResponseDto.getName());

		verify(vetService, times(1)).findAll();
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("수의사 목록 조회 - 빈 리스트일 경우")
	void getAllVet_Empty() {
		when(vetService.findAll()).thenReturn(List.of());

		ResponseEntity<List<VetResponseDto>> response = vetController.getAll();

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().isEmpty()).isTrue();

		verify(vetService, times(1)).findAll();
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("특정 수의사 조회 성공")
	void getVet_Success(){
		when(vetService.findById(1)).thenReturn(expectedVetResponseDto);

		ResponseEntity<VetResponseDto> response = vetController.getVet(1);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(response.getBody().getName()).isEqualTo(expectedVetResponseDto.getName());

		verify(vetService, times(1)).findById(1);
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("수의사 조회 실패 - NOT FOUND")
	void getVet_NotFound() {
		doThrow(new IllegalArgumentException("수의사를 찾을 수 없습니다")).when(vetService).findById(51);

		Throwable exception = assertThrows(IllegalArgumentException.class, () -> vetController.getVet(51));

		assertThat(exception.getMessage()).isEqualTo("수의사를 찾을 수 없습니다");
		verify(vetService, times(1)).findById(51);
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("전문분야별 수의사 조회 성공")
	void getVetBySpecialty_Success(){
		when(vetService.findBySpecialtyId(1)).thenReturn(List.of(expectedVetResponseDto));

		ResponseEntity<List<VetResponseDto>> response = vetController.getVetsBySpecialityId(1);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().size()).isEqualTo(1);
		assertThat(response.getBody().get(0).getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(response.getBody().get(0).getName()).isEqualTo(expectedVetResponseDto.getName());

		verify(vetService, times(1)).findBySpecialtyId(1);
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("전문 분야별 수의사 조회 - 결과 없음")
	void getVetBySpecialty_NoResults() {
		when(vetService.findBySpecialtyId(51)).thenReturn(List.of()); // 결과 없음

		ResponseEntity<List<VetResponseDto>> response = vetController.getVetsBySpecialityId(51);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().isEmpty()).isTrue();

		verify(vetService, times(1)).findBySpecialtyId(51);
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("수의사 전공 수정 성공")
	void updateVetSpeciality_Success(){
		when(vetService.update(1, expectedVetRequestDtoChangeSpecialty)).thenReturn(expectedVetResponseDto);

		ResponseEntity<VetResponseDto> response = vetController.update(1, expectedVetRequestDtoChangeSpecialty);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(response.getBody().getName()).isEqualTo(expectedVetResponseDto.getName());

		verify(vetService, times(1)).update(1, expectedVetRequestDtoChangeSpecialty);
		verifyNoMoreInteractions(vetService);
	}

	@Test
	@DisplayName("수의사 이름 수정 성공")
	void updateVetName_Success(){
		when(vetService.update(1, expectedVetRequestDtoChangeName)).thenReturn(expectedVetResponseDto);

		ResponseEntity<VetResponseDto> response = vetController.update(1, expectedVetRequestDtoChangeName);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(response.getBody().getName()).isEqualTo(expectedVetResponseDto.getName());

		verify(vetService, times(1)).update(1, expectedVetRequestDtoChangeName);
		verifyNoMoreInteractions(vetService);

	}

	// 컨트롤러 수정 필요
	@Test
	@DisplayName("수의사 삭제 성공")
	void deleteVet_Success(){
		vetController.delete(1);

		verify(vetService, times(1)).delete(1);

		verifyNoMoreInteractions(vetService);
	}
}
