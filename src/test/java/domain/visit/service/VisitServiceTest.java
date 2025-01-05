package domain.visit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.samples.petclinic.domain.visit.repository.VisitRepository;
import org.springframework.samples.petclinic.domain.visit.service.VisitService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

	@InjectMocks
	private VisitService visitService;

	@Mock
	private VisitRepository visitRepository;

	@Mock
	private PetRepository petRepository;

	private VisitRequestDto visitRequestDto;
	private Visit visit;
	private Pet pet;

	@BeforeEach
	void setUp() {
		pet = Pet.builder()
			.id(1)
			.name("PetA")
			.build();

		visitRequestDto = VisitRequestDto.builder()
			.description("건강검진")
			.visitDate(LocalDateTime.now())
			.petId(pet.getId())
			.build();

		visit = Visit.builder()
			.id(1)
			.description("건강검진")
			.visitDate(LocalDateTime.now())
			.petId(pet)
			.build();
	}

	@Test
	@DisplayName("방문 내역 생성 성공")
	void createVisit_Success() {

		// Given
		when(petRepository.findById(eq(visitRequestDto.getPetId()))).thenReturn(Optional.of(pet));
		when(visitRepository.save(any(Visit.class))).thenReturn(visit);

		// When
		VisitResponseDto response = visitService.createVisit(visitRequestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getVisitId()).isEqualTo(visit.getId());
		assertThat(response.getPetName()).isEqualTo(pet.getName());
		assertThat(response.getDescription()).isEqualTo(visit.getDescription());

		verify(petRepository, times(1)).findById(eq(visitRequestDto.getPetId()));
		verify(visitRepository, times(1)).save(any(Visit.class));
	}

	@Test
	@DisplayName("방문 내역 생성 실패 - 반려동물 없음")
	void createVisit_Fail_PetNotFound() {
		// Given
		when(petRepository.findById(eq(visitRequestDto.getPetId()))).thenReturn(Optional.empty());

		// When,Then
		assertThatThrownBy(() -> visitService.createVisit(visitRequestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", PetErrorCode.NO_PET)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 반려동물이 존재하지 않습니다.");

		verify(petRepository, times(1)).findById(eq(visitRequestDto.getPetId()));
		verifyNoInteractions(visitRepository);
	}

	@Test
	@DisplayName("특정 반려동물의 방문 내역 조회 성공")
	void getVisitsByPetId_Success() {

		// Given
		when(petRepository.findById(eq(pet.getId()))).thenReturn(Optional.of(pet));
		when(visitRepository.findAllByPetId(eq(pet))).thenReturn(List.of(visit));

		// When
		List<VisitResponseDto> response = visitService.getVisitsByPetId(pet.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response).hasSize(1);
		assertThat(response.get(0).getVisitId()).isEqualTo(visit.getId());
		assertThat(response.get(0).getPetName()).isEqualTo(pet.getName());
		assertThat(response.get(0).getDescription()).isEqualTo(visit.getDescription());


		verify(petRepository, times(1)).findById(eq(pet.getId()));
		verify(visitRepository, times(1)).findAllByPetId(eq(pet));
	}

	@Test
	@DisplayName("특정 반려동물의 방문 내역 조회 실패 - 반려동물 없음")
	void getVisitsByPetId_Fail_PetNotFound() {
		// Given
		when(petRepository.findById(eq(pet.getId()))).thenReturn(Optional.empty());

		// When
		// Then
		assertThatThrownBy(() -> visitService.getVisitsByPetId(pet.getId()))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", PetErrorCode.NO_PET)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 반려동물이 존재하지 않습니다.");



		// Then
		verify(petRepository, times(1)).findById(eq(pet.getId()));
		verifyNoInteractions(visitRepository);
	}
}
