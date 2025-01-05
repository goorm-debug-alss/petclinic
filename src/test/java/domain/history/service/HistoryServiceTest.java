package domain.history.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.history.repository.HistoryRepository;
import org.springframework.samples.petclinic.domain.history.service.HistoryService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.VetRepository;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.samples.petclinic.domain.visit.repository.VisitRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

	@InjectMocks
	private HistoryService historyService;

	@Mock
	private HistoryRepository historyRepository;

	@Mock
	private VetRepository vetRepository;

	@Mock
	private VisitRepository visitRepository;

	@Mock
	private PetRepository petRepository;

	private HistoryRequestDto requestDto;
	private History history;
	private Vet vet;
	private Visit visit;
	private Pet pet;

	@BeforeEach
	void setUp() {
		pet = Pet.builder()
			.id(1)
			.name("Pet A")
			.build();

		vet = Vet.builder()
			.id(1)
			.name("Vet A")
			.build();

		visit = Visit.builder()
			.id(1)
			.description("진료")
			.petId(pet)
			.build();

		requestDto = new HistoryRequestDto("감기", "감기약 처방", 1, 1);

		history = History.builder()
			.id(1)
			.symptoms("감기")
			.content("감기약 처방")
			.vetId(vet)
			.visitId(visit)
			.build();
	}

	@Test
	@DisplayName("진료 내역 생성 성공")
	void addHistory_Success() {
		// Given
		when(vetRepository.findById(eq(requestDto.getVetId()))).thenReturn(Optional.of(vet));
		when(visitRepository.findById(eq(requestDto.getVisitId()))).thenReturn(Optional.of(visit));
		when(historyRepository.save(any(History.class))).thenReturn(history);

		// When
		HistoryResponseDto response = historyService.addHistory(requestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getHistoryId()).isEqualTo(history.getId());
		assertThat(response.getSymptoms()).isEqualTo(history.getSymptoms());
		assertThat(response.getContent()).isEqualTo(history.getContent());

		verify(vetRepository, times(1)).findById(eq(requestDto.getVetId()));
		verify(visitRepository, times(1)).findById(eq(requestDto.getVisitId()));
		verify(historyRepository, times(1)).save(any(History.class));
	}

	@Test
	@DisplayName("특정 반려동물의 진료 내역 조회 성공")
	void getHistoriesByPetId_Success() {
		// Given
		when(petRepository.findById(eq(pet.getId()))).thenReturn(Optional.of(pet));
		when(historyRepository.findAllByVisitId_PetId(eq(pet))).thenReturn(List.of(history));

		// When
		List<HistoryResponseDto> response = historyService.getHistoriesByPetId(pet.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response).hasSize(1);
		assertThat(response.get(0).getHistoryId()).isEqualTo(history.getId());
		assertThat(response.get(0).getSymptoms()).isEqualTo(history.getSymptoms());

		verify(petRepository, times(1)).findById(eq(pet.getId()));
		verify(historyRepository, times(1)).findAllByVisitId_PetId(eq(pet));
	}

	@Test
	@DisplayName("특정 반려동물의 진료 내역 조회 실패 - 반려동물 없음")
	void getHistoriesByPetId_Fail_PetNotFound() {
		// Given
		when(petRepository.findById(eq(pet.getId()))).thenReturn(Optional.empty());

		// When, Then
		assertThatThrownBy(() -> historyService.getHistoriesByPetId(pet.getId()))
			.isInstanceOf(ApiException.class);

		verify(petRepository, times(1)).findById(eq(pet.getId()));
		verifyNoInteractions(historyRepository);
	}

	@Test
	@DisplayName("진료 내역 삭제 성공")
	void deleteHistory_Success() {
		// Given
		when(historyRepository.existsById(eq(history.getId()))).thenReturn(true);

		// When
		historyService.deleteHistory(history.getId());

		// Then
		verify(historyRepository, times(1)).existsById(eq(history.getId()));
		verify(historyRepository, times(1)).deleteById(eq(history.getId()));
	}

	@Test
	@DisplayName("진료 내역 삭제 실패 - 내역 없음")
	void deleteHistory_Fail_HistoryNotFound() {
		// Given
		when(historyRepository.existsById(eq(history.getId()))).thenReturn(false);

		// When, Then
		assertThatThrownBy(() -> historyService.deleteHistory(history.getId()))
			.isInstanceOf(ApiException.class);

		verify(historyRepository, times(1)).existsById(eq(history.getId()));
		verify(historyRepository, times(0)).deleteById(anyInt());
	}
}
