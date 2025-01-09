package domain.history.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.HistoryErrorCode;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.error.VisitErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.mapper.HistoryMapper;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.history.repository.HistoryRepository;
import org.springframework.samples.petclinic.domain.history.service.HistoryService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.enums.VetStatus;
import org.springframework.samples.petclinic.domain.vet.service.VetService;
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
	private VetService vetService;

	@Mock
	private VisitRepository visitRepository;

	@Mock
	private PetRepository petRepository;

	@Mock
	private HistoryMapper historyMapper;

	private HistoryRequestDto requestDto;
	private History history;
	private Vet vet;
	private Visit visit;
	private Pet pet;
	private HistoryResponseDto responseDto;

	@BeforeEach
	void setUp() {
		pet = Pet.builder()
			.id(1)
			.name("Pet A")
			.build();

		vet = Vet.builder()
			.id(1)
			.name("Vet A")
			.status(VetStatus.REGISTERED)
			.build();

		visit = Visit.builder()
			.id(1)
			.description("진료")
			.pet(pet)
			.build();

		requestDto = new HistoryRequestDto("감기", "감기약 처방", 1, 1);

		history = History.builder()
			.id(1)
			.symptoms("감기")
			.content("감기약 처방")
			.vet(vet)
			.visit(visit)
			.build();

		responseDto = HistoryResponseDto.builder()
			.historyId(history.getId())
			.symptoms(history.getSymptoms())
			.content(history.getContent())
			.vetId(history.getVet().getId())
			.visitId(history.getVisit().getId())
			.build();
	}

	@Test
	@DisplayName("진료 내역 추가 성공")
	void addHistory_Success() {
		// Given
		when(vetService.getVetOrThrow(eq(requestDto.getVetId()))).thenReturn(vet);
		when(visitRepository.findById(eq(requestDto.getVisitId()))).thenReturn(Optional.of(visit));
		when(historyMapper.toEntity(eq(requestDto), eq(vet), eq(visit))).thenReturn(history);
		when(historyRepository.save(any(History.class))).thenReturn(history);
		when(historyMapper.toDto(eq(history))).thenReturn(responseDto);

		// When
		HistoryResponseDto result = historyService.addHistory(requestDto);

		// Then
		assertThat(result).isNotNull();
		verify(vetService, times(1)).getVetOrThrow(eq(requestDto.getVetId()));
		verify(visitRepository, times(1)).findById(eq(requestDto.getVisitId()));
		verify(historyMapper, times(1)).toEntity(eq(requestDto), eq(vet), eq(visit));
		verify(historyRepository, times(1)).save(eq(history));
		verify(historyMapper, times(1)).toDto(eq(history));
	}


	@Test
	@DisplayName("진료 내역 생성 실패 - 수의사 없음")
	void addHistory_Fail_VetNotFound() {
		// Given
		when(vetService.getVetOrThrow(eq(requestDto.getVetId()))).thenThrow(new ApiException(VetErrorCode.NO_VET));

		// When, Then
		assertThatThrownBy(() -> historyService.addHistory(requestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", VetErrorCode.NO_VET)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 수의사가 존재하지 않습니다.");

		verify(vetService, times(1)).getVetOrThrow(eq(requestDto.getVetId()));
		verifyNoInteractions(visitRepository);
		verify(historyRepository, times(0)).save(any(History.class));
	}

	@Test
	@DisplayName("진료 내역 생성 실패 - 방문 내역 없음")
	void addHistory_Fail_VisitNotFound() {
		// Given
		when(vetService.getVetOrThrow(eq(requestDto.getVetId()))).thenReturn(vet);
		when(visitRepository.findById(eq(requestDto.getVisitId()))).thenReturn(Optional.empty());

		// When, Then
		assertThatThrownBy(() -> historyService.addHistory(requestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", VisitErrorCode.NO_VISIT)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 방문내역이 존재하지 않습니다.");

		verify(vetService, times(1)).getVetOrThrow(eq(requestDto.getVetId()));
		verify(visitRepository, times(1)).findById(eq(requestDto.getVisitId()));
		verify(historyRepository, times(0)).save(any(History.class));
	}

	@Test
	@DisplayName("특정 반려동물의 진료 내역 조회 성공")
	void getHistoriesByPetId_Success() {
		// Given
		when(petRepository.findById(eq(pet.getId()))).thenReturn(Optional.of(pet));
		when(historyRepository.findAllByVisitId_PetId(eq(pet))).thenReturn(List.of(history));
		when(historyMapper.toDto(eq(history))).thenReturn(responseDto);

		// When
		List<HistoryResponseDto> result = historyService.getHistoriesByPetId(pet.getId());

		// Then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getHistoryId()).isEqualTo(responseDto.getHistoryId());
		verify(historyMapper, times(1)).toDto(eq(history));
	}

	@Test
	@DisplayName("특정 반려동물의 진료 내역 조회 실패 - 반려동물 없음")
	void getHistoriesByPetId_Fail_PetNotFound() {
		// Given
		when(petRepository.findById(eq(pet.getId()))).thenReturn(Optional.empty());

		// When, Then
		assertThatThrownBy(() -> historyService.getHistoriesByPetId(pet.getId()))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", PetErrorCode.NO_PET)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 반려동물이 존재하지 않습니다.");

		verify(petRepository, times(1)).findById(eq(pet.getId()));
		verifyNoInteractions(historyRepository);
	}

	@Test
	@DisplayName("특정 반려동물의 진료 내역 조회 실패 - 진료 내역 없음")
	void getHistoriesByPetId_Fail_HistoryNotFound() {
		// Given
		when(petRepository.findById(eq(pet.getId()))).thenReturn(Optional.of(pet));
		when(historyRepository.findAllByVisitId_PetId(eq(pet))).thenReturn(List.of());

		// When
		List<HistoryResponseDto> response = historyService.getHistoriesByPetId(pet.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response).isEmpty();

		verify(petRepository, times(1)).findById(eq(pet.getId()));
		verify(historyRepository, times(1)).findAllByVisitId_PetId(eq(pet));
	}

	@Test
	@DisplayName("진료 내역 수정 성공")
	void updateHistory_Success() {
		// Given
		when(historyRepository.findById(eq(history.getId()))).thenReturn(Optional.of(history));
		when(vetService.getVetOrThrow(eq(requestDto.getVetId()))).thenReturn(vet);
		when(visitRepository.findById(eq(requestDto.getVisitId()))).thenReturn(Optional.of(visit));
		when(historyRepository.save(any(History.class))).thenReturn(history);
		when(historyMapper.toDto(eq(history))).thenReturn(responseDto);

		// When
		HistoryResponseDto result = historyService.updateHistory(history.getId(), requestDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getHistoryId()).isEqualTo(responseDto.getHistoryId());
		assertThat(result.getSymptoms()).isEqualTo(requestDto.getSymptoms());
		verify(historyMapper, times(1)).toDto(eq(history));
	}


	@Test
	@DisplayName("진료 내역 수정 실패 - 진료 내역 없음")
	void updateHistory_Fail_HistoryNotFound() {
		// Given
		when(historyRepository.findById(eq(history.getId()))).thenReturn(Optional.empty());

		// When, Then
		assertThatThrownBy(() -> historyService.updateHistory(history.getId(), requestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", HistoryErrorCode.NO_HISTORY)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 진료내역이 존재하지 않습니다.");

		verify(historyRepository, times(1)).findById(eq(history.getId()));
		verifyNoInteractions(vetService);
		verifyNoInteractions(visitRepository);
		verify(historyRepository, times(0)).save(any(History.class));
	}

	@Test
	@DisplayName("진료 내역 수정 실패 - 수의사 없음")
	void updateHistory_Fail_VetNotFound() {
		// Given
		when(historyRepository.findById(eq(history.getId()))).thenReturn(Optional.of(history));
		when(vetService.getVetOrThrow(eq(requestDto.getVetId()))).thenThrow(new ApiException(VetErrorCode.NO_VET));

		// When, Then
		assertThatThrownBy(() -> historyService.updateHistory(history.getId(), requestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", VetErrorCode.NO_VET)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 수의사가 존재하지 않습니다.");

		verify(historyRepository, times(1)).findById(eq(history.getId()));
		verify(vetService, times(1)).getVetOrThrow(eq(requestDto.getVetId()));
		verifyNoInteractions(visitRepository);
		verify(historyRepository, times(0)).save(any(History.class));
	}

	@Test
	@DisplayName("진료 내역 수정 실패 - 방문 내역 없음")
	void updateHistory_Fail_VisitNotFound() {
		// Given
		when(historyRepository.findById(eq(history.getId()))).thenReturn(Optional.of(history));
		when(vetService.getVetOrThrow(eq(requestDto.getVetId()))).thenReturn(vet);
		when(visitRepository.findById(eq(requestDto.getVisitId()))).thenReturn(Optional.empty());

		// When, Then
		assertThatThrownBy(() -> historyService.updateHistory(history.getId(), requestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", VisitErrorCode.NO_VISIT)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 방문내역이 존재하지 않습니다.");

		verify(historyRepository, times(1)).findById(eq(history.getId()));
		verify(vetService, times(1)).getVetOrThrow(eq(requestDto.getVetId()));
		verify(visitRepository, times(1)).findById(eq(requestDto.getVisitId()));
		verify(historyRepository, times(0)).save(any(History.class));
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
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", HistoryErrorCode.NO_HISTORY)
			.hasFieldOrPropertyWithValue("errorDescription", "해당 진료내역이 존재하지 않습니다.");

		verify(historyRepository, times(1)).existsById(eq(history.getId()));
		verify(historyRepository, times(0)).deleteById(anyInt());
	}

}
