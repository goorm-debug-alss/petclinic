package domain.visit.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.appointment.repository.AppointmentRepository;
import org.springframework.samples.petclinic.domain.history.repository.HistoryRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.visit.dto.VisitRequestDto;
import org.springframework.samples.petclinic.domain.visit.dto.VisitResponseDto;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.samples.petclinic.domain.visit.repository.VisitRepository;
import org.springframework.samples.petclinic.domain.visit.service.VisitService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = PetClinicApplication.class)
@AutoConfigureMockMvc
@Transactional
class VisitServiceIntegrationTest {

	@Autowired
	private VisitService visitService;

	@Autowired
	private VisitRepository visitRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private HistoryRepository historyRepository;

	private Pet pet;

	@BeforeEach
	void setUp() {
		historyRepository.deleteAll();
		appointmentRepository.deleteAll();
		visitRepository.deleteAll();
		petRepository.deleteAll();

		pet = petRepository.save(Pet.builder().name("Test Pet").build());
	}

	@Test
	@DisplayName("방문 내역 생성 성공")
	void createVisit_Success() {
		// Given
		VisitRequestDto requestDto = new VisitRequestDto(pet.getId(), LocalDateTime.now(), "예방 접종");

		// When
		VisitResponseDto response = visitService.createVisit(requestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getDescription()).isEqualTo("예방 접종");
		assertThat(response.getPetName()).isEqualTo("Test Pet");

		// DB 상태 검증
		List<Visit> visits = visitRepository.findAll();
		assertThat(visits).hasSize(1);
		assertThat(visits.get(0).getDescription()).isEqualTo("예방 접종");
	}

	@Test
	@DisplayName("방문 내역 생성 실패 - 반려동물 없음")
	void createVisit_Fail_NoPet() {
		// Given
		VisitRequestDto requestDto = new VisitRequestDto(-1, LocalDateTime.now(), "건강 검진");

		// When, Then
		assertThatThrownBy(() -> visitService.createVisit(requestDto))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", PetErrorCode.NO_PET);
	}

	@Test
	@DisplayName("특정 반려동물의 방문 내역 조회 성공")
	void getVisitsByPetId_Success() {
		// Given
		visitRepository.save(Visit.builder()
			.description("예방 접종")
			.visitDate(LocalDateTime.now())
			.pet(pet)
			.build());

		// When
		List<VisitResponseDto> response = visitService.getVisitsByPetId(pet.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response).hasSize(1);
		assertThat(response.get(0).getDescription()).isEqualTo("예방 접종");
	}

	@Test
	@DisplayName("특정 반려동물의 방문 내역 조회 실패 - 반려동물 없음")
	void getVisitsByPetId_Fail_NoPet() {
		// Given
		int invalidPetId = -1;

		// When, Then
		assertThatThrownBy(() -> visitService.getVisitsByPetId(invalidPetId))
			.isInstanceOf(ApiException.class)
			.hasFieldOrPropertyWithValue("errorCodeInterface", PetErrorCode.NO_PET);
	}
}
