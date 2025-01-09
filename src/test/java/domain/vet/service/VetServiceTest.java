package domain.vet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.vet.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.domain.vet.mapper.VetSpecialtyMapper;
import org.springframework.samples.petclinic.domain.vet.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetSpecialtyRepository;
import org.springframework.samples.petclinic.domain.vet.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.mapper.VetMapper;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.VetSpeciality;
import org.springframework.samples.petclinic.domain.vet.service.SpecialtyService;
import org.springframework.samples.petclinic.domain.vet.service.VetService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VetServiceTest {
	@InjectMocks
	private VetService vetService;

	@Mock
	private VetRepository vetRepository;

	@Mock
	private SpecialtyRepository specialtyRepository;

	@Mock
	private VetMapper vetMapper;

	@Mock
	private VetSpecialtyRepository vetSpecialtyRepository;

	@Mock
	private SpecialtyService specialtyService;

	@Mock
	private VetSpecialtyMapper vetSpecialtyMapper;

	private Vet vet;
	private Specialty specialty;
	private Specialty specialty2;
	private VetRequestDto vetRequestDto;
	private VetResponseDto expectedVetResponseDto;
	private VetSpeciality vetSpeciality;

	@BeforeEach
	@DisplayName("Sample Data")
	void setUp() {
		sampleVet();
		sampleVetRequestDto();
		sampleSpeciality();
		sampleSpeciality2();
		sampleVetSpeciality();
		sampleExpectedVetResponse();
	}

	void sampleVet(){
		vet = Vet.builder()
			.id(1)
			.name("테스트용")
			.build();
	}

	void sampleVetRequestDto(){
		vetRequestDto = new VetRequestDto();
		vetRequestDto.setName("DTO테스트용");
		vetRequestDto.setSpecialties(new ArrayList<>(List.of(1)));
	}

	void sampleSpeciality(){
		specialty = new Specialty();
		specialty.setId(1);
		specialty.setName("외과");
	}

	void sampleVetSpeciality() {
		vetSpeciality = VetSpeciality.builder()
			.id(1)
			.vet(vet)
			.specialty(specialty)
			.build();
	}

	void sampleExpectedVetResponse(){
		expectedVetResponseDto = new VetResponseDto(vet.getId(), vet.getName(), null, null, List.of(specialty));
	}

	void sampleSpeciality2(){
		specialty2 = new Specialty();
		specialty2.setId(2);
		specialty2.setName("소아과");
	}

	@Test
	@DisplayName("수의사 등록 성공")
	void registerVetSuccess() {
		when(vetMapper.toEntity(vetRequestDto)).thenReturn(vet);
		when(vetRepository.save(any(Vet.class))).thenReturn(vet);
		when(specialtyService.findByIds(vetRequestDto.getSpecialties())).thenReturn(List.of(specialty));
		when(vetSpecialtyMapper.toEntityList(vet, List.of(specialty))).thenReturn(List.of(vetSpeciality));
		when(vetMapper.toResponse(vet)).thenReturn(expectedVetResponseDto);

		VetResponseDto vetResponseDto = vetService.register(vetRequestDto);

		assertThat(vetResponseDto).isNotNull();
		assertThat(vetResponseDto.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(vetResponseDto.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(vetResponseDto.getSpecialties()).isNotEmpty();
		assertThat(vetResponseDto.getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetMapper, times(1)).toEntity(vetRequestDto);
		verify(vetRepository, times(1)).save(any(Vet.class));
		verify(specialtyService, times(1)).findByIds(vetRequestDto.getSpecialties());
		verify(vetSpecialtyMapper, times(1)).toEntityList(vet, List.of(specialty));
		verify(vetSpecialtyRepository, times(1)).saveAll(any());
		verify(vetMapper, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, specialtyService, vetSpecialtyRepository, vetMapper);
	}


	@Test
	@DisplayName("수의사 등록 실패 - 이름이 null일 때")
	void registerVetFailure_nullName() {
		vetRequestDto.setName(null);

		assertThatThrownBy(() -> vetService.register(vetRequestDto)).isInstanceOf(ApiException.class)
			.hasMessageContaining("이름은 필수값 입니다.");

		verify(vetRepository, never()).save(any());
		verify(specialtyRepository, never()).save(any());
	}

	@Test
	@DisplayName("수의사 등록 실패 - 전문 분야가 null일 때")
	void registerVetFailure_nullSpecialties() {
		vetRequestDto.setSpecialties(null);

		assertThatThrownBy(() -> vetService.register(vetRequestDto)).isInstanceOf(ApiException.class)
			.hasMessageContaining("전공분야는 필수값 입니다.");

		verify(vetRepository, never()).save(any());
		verify(specialtyRepository, never()).save(any());
	}

	@Test
	@DisplayName("수의사 등록 실패 - 전문 분야가 존재하지 않을 때")
	void registerVetFailure_noSpecialties() {
		vetRequestDto.setSpecialties(new ArrayList<>(List.of(222)));
		when(specialtyService.findByIds(new ArrayList<>(List.of(222))))
			.thenThrow(new ApiException(VetErrorCode.NO_SPECIALITY));

		assertThatThrownBy(() -> vetService.register(vetRequestDto))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 전공분야가 존재하지 않습니다.");

		verify(vetRepository, never()).save(any());
		verify(specialtyRepository, never()).save(any());
	}

	@Test
	@DisplayName("수의사 전체 조회 성공")
	void viewAllVetSuccess() {
		when(vetRepository.findAllByOrderById()).thenReturn(List.of(vet));
		when(vetMapper.toResponse(vet)).thenReturn(expectedVetResponseDto);

		List<VetResponseDto> result = vetService.findAll();

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.get(0).getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.get(0).getSpecialties()).isNotEmpty();
		assertThat(result.get(0).getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetRepository, times(1)).findAllByOrderById();
		verify(vetMapper, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, vetMapper);
	}

	@Test
	@DisplayName("수의사 전체 조회 실패 - 빈 리스트 반환")
	void viewAllVetFailure_emptyList() {
		when(vetRepository.findAllByOrderById()).thenReturn(List.of());

		List<VetResponseDto> result = vetService.findAll();

		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(vetRepository, times(1)).findAllByOrderById();
		verifyNoInteractions(vetMapper);
	}

	@Test
	@DisplayName("수의사 전체 조회 실패 - 예외 발생")
	void viewAllVetFailure_exception() {
		when(vetRepository.findAllByOrderById()).thenThrow(new RuntimeException("수의사 전체 조회 예외 발생"));

		assertThatThrownBy(() -> vetService.findAll()).isInstanceOf(RuntimeException.class)
			.hasMessage("수의사 전체 조회 예외 발생");

		verify(vetRepository, times(1)).findAllByOrderById();
		verifyNoInteractions(vetMapper);
	}

	@Test
	@DisplayName("특정 수의사 조회 성공")
	void viewVetByIdSuccess() {
		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(vetMapper.toResponse(vet)).thenReturn(expectedVetResponseDto);

		VetResponseDto result = vetService.findById(1);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).isNotEmpty();
		assertThat(result.getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetRepository, times(1)).findById(1);
		verify(vetMapper, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, vetMapper);
	}

	@Test
	@DisplayName("특정 수의사 조회 실패 - 해당 아이디에 해당하는 수의사가 존재하지 않을 때")
	void viewVetByIdFailure_notFound() {
		when(vetRepository.findById(2)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> vetService.findById(2))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 수의사가 존재하지 않습니다.");

		verify(vetRepository, times(1)).findById(2);
		verifyNoInteractions(vetMapper);
	}

	@Test
	@DisplayName("전문 분아별 수의사 조회 성공")
	void viewVetBySpecialtySuccess() {
		when(vetSpecialtyRepository.findVetIdsBySpecialtyId_Id(1)).thenReturn(List.of(vetSpeciality));
		when(vetRepository.findAllById(List.of(vet.getId()))).thenReturn(List.of(vet));
		when(vetMapper.toResponse(vet)).thenReturn(expectedVetResponseDto);

		List<VetResponseDto> result = vetService.findBySpecialtyId(1);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.get(0).getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.get(0).getSpecialties()).isNotEmpty();
		assertThat(result.get(0).getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetSpecialtyRepository, times(1)).findVetIdsBySpecialtyId_Id(1);
		verify(vetRepository, times(1)).findAllById(List.of(vet.getId()));
		verify(vetMapper, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetSpecialtyRepository, vetRepository, vetMapper);
	}

	@Test
	@DisplayName("전문 분아별 수의사 조회 실패 - 전문 분야 아이디가 존재하지 않을 때")
	void viewVetBySpecialtyFailure_specialityIdNotFound() {
		when(vetSpecialtyRepository.findVetIdsBySpecialtyId_Id(123)).thenReturn(List.of());

		assertThatThrownBy(() -> vetService.findBySpecialtyId(123))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 전공분야가 존재하지 않습니다.");

		verify(vetSpecialtyRepository, times(1)).findVetIdsBySpecialtyId_Id(123);
		verifyNoMoreInteractions(vetSpecialtyRepository, vetRepository, vetMapper);
	}

	@Test
	@DisplayName("수의사 삭제 성공")
	void deleteVetSuccess() {
		when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
		doNothing().when(vetRepository).delete(vet);

		vetService.delete(1);

		verify(vetRepository, times(1)).findById(1);
		verify(vetRepository, times(1)).delete(vet);
		verify(vetSpecialtyRepository, times(1)).deleteAllByVetId_Id(1);

		verifyNoMoreInteractions(vetRepository);
	}

	@Test
	@DisplayName("수의사 삭제 성공 - 관련 전공 연결 삭제 확인")
	void deleteVetSuccess_withSpecialities() {
		when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
		doNothing().when(vetRepository).delete(vet);

		vetService.delete(1);

		verify(vetSpecialtyRepository, times(1)).deleteAllByVetId_Id(1);
		verify(vetRepository, times(1)).delete(vet);

		verifyNoMoreInteractions(vetRepository, vetSpecialtyRepository);
	}


	@Test
	@DisplayName("수의사 삭제 실패 - 해당 아이디에 해당하는 수의사가 존재하지 않을 때")
	void deleteVetFailure_notFound() {
		when(vetRepository.findById(222)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> vetService.delete(222))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 수의사가 존재하지 않습니다.");

		verify(vetRepository, never()).delete(any());

		verifyNoMoreInteractions(vetRepository, vetSpecialtyRepository);
	}

	@Test
	@DisplayName("수의사 전공 수정 성공")
	void updateVetSuccess_Speciality() {
		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(specialtyService.findByIds(new ArrayList<>(List.of(2)))).thenReturn(List.of(specialty2));
		when(vetSpecialtyMapper.toEntityList(vet, List.of(specialty2))).thenReturn(List.of(vetSpeciality));
		when(vetMapper.toResponse(vet)).thenReturn(
			new VetResponseDto(vet.getId(), vet.getName(), null, null, List.of(specialty2)));

		VetRequestDto updateDto = new VetRequestDto(null, new ArrayList<>(List.of(2)));

		VetResponseDto result = vetService.update(1, updateDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).isNotEmpty();
		assertThat(result.getSpecialties().get(0).getId()).isEqualTo(2);
		assertThat(result.getSpecialties().get(0).getName()).isEqualTo("소아과");

		verify(vetRepository, times(1)).findById(1);
		verify(vetSpecialtyRepository, times(1)).deleteAllByVetId_Id(1);
		verify(specialtyService, times(1)).findByIds(new ArrayList<>(List.of(2)));
		verify(vetSpecialtyMapper, times(1)).toEntityList(vet, List.of(specialty2));
		verify(vetSpecialtyRepository, times(1)).saveAll(List.of(vetSpeciality)); // saveAll 검증
		verify(vetRepository, times(1)).save(any(Vet.class));
		verify(vetMapper, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, vetSpecialtyRepository, vetMapper, specialtyService);
	}

	@Test
	@DisplayName("수의사 이름 변경 성공")
	void updateVetSuccess_Name() {
		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(vetMapper.toResponse(vet)).thenReturn(expectedVetResponseDto);

		VetRequestDto updateDto = new VetRequestDto("이름수정", null);
		VetResponseDto result = vetService.update(1, updateDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).isNotEmpty();
		assertThat(result.getSpecialties().size()).isEqualTo(1);

		verify(vetRepository, times(1)).findById(1);
		verify(vetRepository, times(1)).save(any(Vet.class));
		verify(vetMapper, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, vetMapper);
	}

	@Test
	@DisplayName("수의사 정보 변경 실패 - 해당 아이디 수의사를 찾을 수 없는 경우")
	void updateVetFailure_notFound() {
		when(vetRepository.findById(222)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> vetService.update(222, vetRequestDto))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 수의사가 존재하지 않습니다.");

		verify(vetRepository, times(1)).findById(222);
		verifyNoInteractions(vetMapper);
	}

	@Test
	@DisplayName("수의사 전공 수정 실패 - 전공이 없는 경우")
	void updateVetFailure_NoSpeciality() {
		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(specialtyService.findByIds(new ArrayList<>(List.of(222))))
			.thenThrow(new ApiException(VetErrorCode.NO_SPECIALITY));

		assertThatThrownBy(() -> vetService.update(1, new VetRequestDto(null, List.of(222))))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 전공분야가 존재하지 않습니다.");

		verify(vetSpecialtyRepository, never()).deleteAllByVetId_Id(anyInt());
		verify(vetRepository, never()).save(any());
		verify(vetSpecialtyRepository, never()).saveAll(any());
	}
}
