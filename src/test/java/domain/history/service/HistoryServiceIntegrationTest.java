package domain.history.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.common.error.HistoryErrorCode;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.error.VetErrorCode;
import org.springframework.samples.petclinic.common.error.VisitErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.history.dto.HistoryRequestDto;
import org.springframework.samples.petclinic.domain.history.dto.HistoryResponseDto;
import org.springframework.samples.petclinic.domain.history.model.History;
import org.springframework.samples.petclinic.domain.history.repository.HistoryRepository;
import org.springframework.samples.petclinic.domain.history.service.HistoryService;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetRepository;
import org.springframework.samples.petclinic.domain.vet.repository.VetSpecialtyRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.samples.petclinic.domain.visit.repository.VisitRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = PetClinicApplication.class)
@AutoConfigureMockMvc
@Transactional
class HistoryServiceIntegrationTest {

	@Autowired
	private HistoryService historyService;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private VetRepository vetRepository;

	@Autowired
	private VetSpecialtyRepository vetSpecialtyRepository;

	@Autowired
	private VisitRepository visitRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	private Pet pet;
	private Vet vet;
	private Visit visit;

	@BeforeEach
	void setUp() {
		appointmentRepository.deleteAll();
		vetSpecialtyRepository.deleteAll();
		vetRepository.deleteAll();
		historyRepository.deleteAll();
		visitRepository.deleteAll();
		petRepository.deleteAll();

		pet = petRepository.save(Pet.builder().name("Pet A").build());
		vet = vetRepository.save(Vet.builder().name("Vet A").build());
		visit = visitRepository.save(Visit.builder().description("Visit A").petId(pet).build());
	}

	@Test
	@DisplayName("진료 내역 생성 성공")
	void addHistory_Success() {
		// Given
		HistoryRequestDto requestDto = new HistoryRequestDto("감기", "감기약 처방", vet.getId(), visit.getId());

		// When
		HistoryResponseDto response = historyService.addHistory(requestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getSymptoms()).isEqualTo("감기");
		assertThat(response.getContent()).isEqualTo("감기약 처방");

		// DB 상태 검증
		assertThat(historyRepository.findAll()).hasSize(1);
	}

	@Test
	@DisplayName("진료 내역 생성 실패 - 방문내역 없음")
	void addHistory_Fail_InvalidVisitId() {
		// Given
		HistoryRequestDto requestDto = new HistoryRequestDto("감기", "감기약 처방", vet.getId(), -1);

		// When, Then
		assertThatThrownBy(() -> historyService.addHistory(requestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", VisitErrorCode.NO_VISIT);
	}

	@Test
	@DisplayName("특정 반려동물의 진료 내역 조회 성공")
	void getHistoriesByPetId_Success() {
		// Given
		historyRepository.save(History.builder()
			.symptoms("감기")
			.content("감기약 처방")
			.vetId(vet)
			.visitId(visit)
			.build());

		// When
		List<HistoryResponseDto> response = historyService.getHistoriesByPetId(pet.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response).hasSize(1);
		assertThat(response.get(0).getSymptoms()).isEqualTo("감기");
		assertThat(response.get(0).getContent()).isEqualTo("감기약 처방");
	}

	@Test
	@DisplayName("특정 반려동물의 진료 내역 조회 실패 - 반려동물 없음")
	void getHistoriesByPetId_Fail_NoPet() {
		// Given
		int invalidPetId = -1;

		// When, Then
		assertThatThrownBy(() -> historyService.getHistoriesByPetId(invalidPetId))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", PetErrorCode.NO_PET);
	}

	@Test
	@DisplayName("진료 내역 수정 성공")
	void updateHistory_Success() {
		// Given
		History history = historyRepository.save(History.builder()
			.symptoms("감기")
			.content("감기약 처방")
			.vetId(vet)
			.visitId(visit)
			.build());

		HistoryRequestDto updateRequest = new HistoryRequestDto("알러지", "알러지 치료", vet.getId(), visit.getId());

		// When
		HistoryResponseDto response = historyService.updateHistory(history.getId(), updateRequest);

		// Then
		assertThat(response.getSymptoms()).isEqualTo("알러지");
		assertThat(response.getContent()).isEqualTo("알러지 치료");

		History updatedHistory = historyRepository.findById(history.getId()).orElseThrow();
		assertThat(updatedHistory.getSymptoms()).isEqualTo("알러지");
		assertThat(updatedHistory.getContent()).isEqualTo("알러지 치료");
	}

	@Test
	@DisplayName("진료 내역 수정 실패 - 수의사 없음")
	void updateHistory_Fail_NoVet() {
		// Given
		History history = historyRepository.save(History.builder()
			.symptoms("감기")
			.content("감기약 처방")
			.vetId(vet)
			.visitId(visit)
			.build());

		HistoryRequestDto updateRequest = new HistoryRequestDto("알러지", "알러지 치료", -1, visit.getId());

		// When, Then
		assertThatThrownBy(() -> historyService.updateHistory(history.getId(), updateRequest))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", VetErrorCode.NO_VET);
	}

	@Test
	@DisplayName("진료 내역 삭제 성공")
	void deleteHistory_Success() {
		// Given
		History history = historyRepository.save(History.builder()
			.symptoms("감기")
			.content("감기약 처방")
			.vetId(vet)
			.visitId(visit)
			.build());

		// When
		historyService.deleteHistory(history.getId());

		// Then
		assertThat(historyRepository.findById(history.getId())).isEmpty();
		assertThat(historyRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("진료 내역 삭제 실패 - 진료 내역 없음")
	void deleteHistory_Fail_HistoryNotFound() {
		// Given
		int invalidId = -1;

		// When, Then
		assertThatThrownBy(() -> historyService.deleteHistory(invalidId))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", HistoryErrorCode.NO_HISTORY);
	}
}
