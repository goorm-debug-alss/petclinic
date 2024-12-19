package domain.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.speciality.SpecialityRepository;
import org.springframework.samples.petclinic.domain.speciality.dto.SpecialityRequestDto;
import org.springframework.samples.petclinic.domain.speciality.model.Specialty;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.vet.VetSpecialityRepository;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.convert.VetConvert;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.VetSpecialty;
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
	private SpecialityRepository specialityRepository;

	@Mock
	private VetConvert vetConvert;

	@Mock
	private VetSpecialityRepository vetSpecialityRepository;

	private Vet vet;
	private Specialty specialty;
	private Specialty specialty2;
	private VetRequestDto vetRequestDto;
	private List<SpecialityRequestDto> specialityRequestDto;
	private VetResponseDto expectedVetResponseDto;
	private VetSpecialty vetSpecialty;

	@BeforeEach
	@DisplayName("Sample Data")
	void setUp() {
		sampleVet();
		sampleSpecialityRequestDto();
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
		vetRequestDto.setSpecialties(specialityRequestDto);
	}

	void sampleSpecialityRequestDto(){
		specialityRequestDto = new ArrayList<>();

		SpecialityRequestDto speciality = new SpecialityRequestDto();
		speciality.setName("외과");

		specialityRequestDto.add(speciality);
	}

	void sampleSpeciality(){
		specialty = new Specialty();
		specialty.setId(1);
		specialty.setName("외과");
	}

	void sampleVetSpeciality() {
		vetSpecialty = VetSpecialty.builder()
			.id(1)
			.vetId(vet) // Vet 객체 재활용
			.specialtyId(specialty) // Specialty 객체 재활용
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
	void registerVetSuccess(){
		when(vetRepository.save(any(Vet.class))).thenReturn(vet);
		when(specialityRepository.findByName("외과")).thenReturn(Optional.empty());
		when(specialityRepository.save(any(Specialty.class))).thenReturn(specialty);
		when(vetConvert.toResponse(vet)).thenReturn(expectedVetResponseDto);

		VetResponseDto vetResponseDto = vetService.register(vetRequestDto);

		assertThat(vetResponseDto).isNotNull();
		assertThat(vetResponseDto.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(vetResponseDto.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(vetResponseDto.getSpecialties()).isNotEmpty();
		assertThat(vetResponseDto.getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetRepository, times(1)).save(any(Vet.class));
		verify(specialityRepository, times(1)).findByName("외과");
		verify(specialityRepository, times(1)).save(any(Specialty.class));
		verify(vetSpecialityRepository, times(1)).save(any());
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, specialityRepository, vetSpecialityRepository, vetConvert);
	}

	// 서비스 수정 필요
	@Test
	@DisplayName("수의사 등록 실패 - 이름이 null일 때")
	void registerVetFailure_nullName() {
		vetRequestDto.setName(null);

		assertThatThrownBy(() -> vetService.register(vetRequestDto)).isInstanceOf(NullPointerException.class);

		verify(vetRepository, never()).save(any());
		verify(specialityRepository, never()).save(any());
	}

	// 서비스 수정 필요
	@Test
	@DisplayName("수의사 등록 실패 - 전문 분야가 null일 때")
	void registerVetFailure_nullSpecialties() {
		vetRequestDto.setSpecialties(null);

		assertThatThrownBy(() -> vetService.register(vetRequestDto)).isInstanceOf(NullPointerException.class);

		verify(vetRepository, never()).save(any());
		verify(specialityRepository, never()).save(any());
	}

	@Test
	@DisplayName("수의사 전체 조회 성공")
	void viewAllVetSuccess() {
		when(vetRepository.findAllByOrderById()).thenReturn(List.of(vet));
		when(vetConvert.toResponse(vet)).thenReturn(expectedVetResponseDto);

		List<VetResponseDto> result = vetService.findAll();

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.get(0).getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.get(0).getSpecialties()).isNotEmpty();
		assertThat(result.get(0).getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetRepository, times(1)).findAllByOrderById();
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, vetConvert);
	}

	@Test
	@DisplayName("수의사 전체 조회 실패 - 빈 리스트 반환")
	void viewAllVetFailure_emptyList() {
		when(vetRepository.findAllByOrderById()).thenReturn(List.of());

		List<VetResponseDto> result = vetService.findAll();

		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(vetRepository, times(1)).findAllByOrderById();
		verifyNoInteractions(vetConvert);
	}

	@Test
	@DisplayName("수의사 전체 조회 실패 - 예외 발생")
	void viewAllVetFailure_exception() {
		when(vetRepository.findAllByOrderById()).thenThrow(new RuntimeException("수의사 전체 조회 예외 발생"));

		assertThatThrownBy(() -> vetService.findAll()).isInstanceOf(RuntimeException.class)
			.hasMessage("수의사 전체 조회 예외 발생");

		verify(vetRepository, times(1)).findAllByOrderById();
		verifyNoInteractions(vetConvert);
	}

	@Test
	@DisplayName("특정 수의사 조회 성공")
	void viewVetByIdSuccess() {
		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(vetConvert.toResponse(vet)).thenReturn(expectedVetResponseDto);

		VetResponseDto result = vetService.findById(1);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).isNotEmpty();
		assertThat(result.getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetRepository, times(1)).findById(1);
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, vetConvert);
	}

	@Test
	@DisplayName("특정 수의사 조회 실패 - 해당 아이디에 해당하는 수의사가 존재하지 않을 때")
	void viewVetByIdFailure_notFound() {
		when(vetRepository.findById(2)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> vetService.findById(2))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("수의사를 찾을 수 없습니다");

		verify(vetRepository, times(1)).findById(2);
		verifyNoInteractions(vetConvert);
	}

	@Test
	@DisplayName("전문 분아별 수의사 조회 성공")
	void viewVetBySpecialtySuccess() {
		when(vetSpecialityRepository.findVetIdsBySpecialtyId_Id(1)).thenReturn(List.of(vetSpecialty));
		when(vetRepository.findAllById(List.of(vet.getId()))).thenReturn(List.of(vet));
		when(vetConvert.toResponse(vet)).thenReturn(expectedVetResponseDto);

		List<VetResponseDto> result = vetService.findBySpecialtyId(1);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.get(0).getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.get(0).getSpecialties()).isNotEmpty();
		assertThat(result.get(0).getSpecialties().get(0).getName()).isEqualTo(specialty.getName());

		verify(vetSpecialityRepository, times(1)).findVetIdsBySpecialtyId_Id(1);
		verify(vetRepository, times(1)).findAllById(List.of(vet.getId()));
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetSpecialityRepository, vetRepository, vetConvert);
	}

	@Test
	@DisplayName("전문 분아별 수의사 조회 실패 - 전문 분야 아이디가 존재하지 않을 때")
	void viewVetBySpecialtyFailure_specialityIdNotFound() {
		when(vetSpecialityRepository.findVetIdsBySpecialtyId_Id(123)).thenReturn(List.of());
		when(vetRepository.findAllById(List.of())).thenReturn(List.of());

		List<VetResponseDto> result = vetService.findBySpecialtyId(123);

		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(vetSpecialityRepository, times(1)).findVetIdsBySpecialtyId_Id(123);
		verify(vetRepository, times(1)).findAllById(List.of());
		verifyNoMoreInteractions(vetSpecialityRepository, vetRepository, vetConvert);
	}

	@Test
	@DisplayName("수의사 삭제 성공")
	void deleteVetSuccess() {
		doNothing().when(vetRepository).deleteById(1);

		vetService.delete(1);

		verify(vetRepository, times(1)).deleteById(1);
		verifyNoMoreInteractions(vetRepository);
	}

	@Test
	@DisplayName("수의사 삭제 성공 - 관련 전공 연결 삭제 확인")
	void deleteVetSuccess_withSpecialities() {
		doNothing().when(vetRepository).deleteById(1);
		doNothing().when(vetSpecialityRepository).deleteAllByVetId_Id(1);

		vetService.delete(1);

		verify(vetSpecialityRepository, times(1)).deleteAllByVetId_Id(1);
		verify(vetRepository, times(1)).deleteById(1);

		verifyNoMoreInteractions(vetRepository, vetSpecialityRepository);
	}

	// 서비스 수정 필요
	@Test
	@DisplayName("수의사 삭제 실패 - 해당 아이디에 해당하는 수의사가 존재하지 않을 때")
	void deleteVetFailure_notFound() {
		doNothing().when(vetRepository).deleteById(222);

		vetService.delete(222);

		verify(vetRepository, times(1)).deleteById(222);
		verifyNoMoreInteractions(vetRepository);
	}

	@Test
	@DisplayName("수의사 전공 수정 성공 - 전공이 없는 경우")
	void updateVetSuccess_NoSpeciality() {
		Specialty savedSpecialty = Specialty.builder()
			.id(5)
			.name("내과")
			.build(); // 변경할 전공

		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(specialityRepository.findByName("내과")).thenReturn(Optional.empty());
		when(specialityRepository.save(any(Specialty.class))).thenReturn(savedSpecialty);
		when(vetConvert.toResponse(vet)).thenReturn(
			new VetResponseDto(vet.getId(), vet.getName(), null, null, List.of(savedSpecialty))
		);

		VetRequestDto updateDto = new VetRequestDto(null, List.of(new SpecialityRequestDto("내과")));

		VetResponseDto result = vetService.update(1, updateDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).isNotEmpty();
		assertThat(result.getSpecialties().get(0).getName()).isEqualTo("내과");

		verify(vetRepository, times(1)).findById(1);
		verify(vetSpecialityRepository, times(1)).deleteAllByVetId_Id(1);
		verify(specialityRepository, times(1)).findByName("내과");
		verify(specialityRepository, times(1)).save(any(Specialty.class));
		verify(vetSpecialityRepository, times(1)).save(any(VetSpecialty.class));
		verify(vetRepository, times(1)).save(any(Vet.class));
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, specialityRepository, vetSpecialityRepository, vetConvert);
	}

	@Test
	@DisplayName("수의사 전공 수정 성공 - 전공이 있는 경우")
	void updateVetSuccess_Speciality() {
		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(specialityRepository.findByName("소아과")).thenReturn(Optional.ofNullable(specialty2));
		when(vetConvert.toResponse(vet)).thenReturn(
			new VetResponseDto(vet.getId(), vet.getName(), null, null, List.of(specialty2))
		);

		VetRequestDto updateDto = new VetRequestDto(null, List.of(new SpecialityRequestDto("소아과")));

		VetResponseDto result = vetService.update(1, updateDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).isNotEmpty();
		assertThat(result.getSpecialties().get(0).getName()).isEqualTo("소아과");

		verify(vetRepository, times(1)).findById(1);
		verify(vetSpecialityRepository, times(1)).deleteAllByVetId_Id(1);
		verify(specialityRepository, times(1)).findByName("소아과");
		verify(vetSpecialityRepository, times(1)).save(any(VetSpecialty.class));
		verify(vetRepository, times(1)).save(any(Vet.class));
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, specialityRepository, vetSpecialityRepository, vetConvert);
	}

	@Test
	@DisplayName("수의사 이름 변경 성공")
	void updateVetSuccess_Name() {
		when(vetRepository.findById(1)).thenReturn(Optional.ofNullable(vet));
		when(vetConvert.toResponse(vet)).thenReturn(expectedVetResponseDto);

		VetRequestDto updateDto = new VetRequestDto("이름수정", null);
		VetResponseDto result = vetService.update(1, updateDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).isNotEmpty();
		assertThat(result.getSpecialties().size()).isEqualTo(1);

		verify(vetRepository, times(1)).findById(1);
		verify(vetRepository, times(1)).save(any(Vet.class));
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, vetConvert);
	}

	@Test
	@DisplayName("수의사 정보 변경 실패 - 해당 아이디 수의사를 찾을 수 없는 경우")
	void updateVetFailure_notFound() {
		when(vetRepository.findById(2)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> vetService.update(2, vetRequestDto))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("수의사를 찾을 수 없습니다");

		verify(vetRepository, times(1)).findById(2);
		verifyNoInteractions(vetConvert);
	}

	// 서비스 수정 필요
	@Test
	@DisplayName("수의사 전공 수정 성공 - 기존 전공과 동일한 전공으로 변경")
	void updateVetSuccess_SameSpeciality() {
		when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
		when(specialityRepository.findByName("외과")).thenReturn(Optional.of(specialty));
		when(vetConvert.toResponse(vet)).thenReturn(expectedVetResponseDto);

		VetRequestDto updateDto = new VetRequestDto(null, List.of(new SpecialityRequestDto("외과")));
		VetResponseDto result = vetService.update(1, updateDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedVetResponseDto.getId());
		assertThat(result.getName()).isEqualTo(expectedVetResponseDto.getName());
		assertThat(result.getSpecialties()).hasSize(1);
		assertThat(result.getSpecialties().get(0).getName()).isEqualTo("외과");

		verify(vetRepository, times(1)).findById(1);
		verifyNoInteractions(vetSpecialityRepository);
		verify(vetRepository, times(1)).save(vet);
		verify(vetConvert, times(1)).toResponse(vet);

		verifyNoMoreInteractions(vetRepository, specialityRepository, vetSpecialityRepository, vetConvert);
	}

}
