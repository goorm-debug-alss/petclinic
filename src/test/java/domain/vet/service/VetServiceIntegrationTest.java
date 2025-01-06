package domain.vet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetRequestDto;
import org.springframework.samples.petclinic.domain.vet.controller.dto.VetResponseDto;
import org.springframework.samples.petclinic.domain.vet.service.VetService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = PetClinicApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class VetServiceIntegrationTest {

	static final MySQLContainer<?> mysqlContainer;

	static {
		mysqlContainer = new MySQLContainer<>("mysql:8.0.32")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");
		mysqlContainer.start();
		System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
		System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
		System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mysqlContainer::getUsername);
		registry.add("spring.datasource.password", mysqlContainer::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
		registry.add("spring.sql.init.mode", () -> "always");
		registry.add("spring.sql.init.schema-locations", () -> "classpath:db/mysql/schema.sql");
		registry.add("spring.sql.init.data-locations", () -> "classpath:db/mysql/data.sql");
	}

	@Autowired
	private VetService vetService;

	private VetRequestDto vetRequestDto;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		sampleVetRequestDto();
	}

	void sampleVetRequestDto(){
		vetRequestDto = new VetRequestDto();
		vetRequestDto.setName("테스트용");
		vetRequestDto.setSpecialties(new ArrayList<>(List.of(1, 2)));
	}

	@Test
	@DisplayName("수의사 등록 성공")
	void registerVetSuccess() {
		VetResponseDto responseDto = vetService.register(vetRequestDto);

		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getName()).isEqualTo("테스트용");
		assertThat(responseDto.getSpecialties()).hasSize(2);
	}

	@Test
	@DisplayName("수의사 삭제 성공")
	void deleteVetSuccess() {
		VetResponseDto savedVet = vetService.register(vetRequestDto);

		vetService.delete(savedVet.getId());

		assertThatThrownBy(() -> vetService.findById(savedVet.getId()))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 수의사가 존재하지 않습니다.");
	}

	@Test
	@DisplayName("수의사 수정 성공")
	void updateVetSuccess() {
		vetRequestDto.setSpecialties(new ArrayList<>(List.of(1)));
		VetResponseDto savedVet = vetService.register(vetRequestDto);

		VetRequestDto updateRequest = new VetRequestDto();
		updateRequest.setName("수정테스트");
		updateRequest.setSpecialties(List.of(1, 2));

		VetResponseDto updatedVet = vetService.update(savedVet.getId(), updateRequest);

		assertThat(updatedVet).isNotNull();
		assertThat(updatedVet.getName()).isEqualTo("수정테스트");
		assertThat(updatedVet.getSpecialties()).hasSize(2);
		assertThat(updatedVet.getSpecialties().get(1).getName()).isEqualTo("소아과");
	}

	@Test
	@DisplayName("수의사 전체 조회 성공")
	void findAllVetsSuccess() {
		List<VetResponseDto> vets = vetService.findAll();

		assertThat(vets).isNotEmpty();
		assertThat(vets).hasSize(2);
		assertThat(vets.get(0).getName()).isEqualTo("이의사");
	}

	@Test
	@DisplayName("특정 수의사 조회 성공")
	void findVetByIdSuccess() {
		VetResponseDto foundVet = vetService.findById(1);

		assertThat(foundVet).isNotNull();
		assertThat(foundVet.getName()).isEqualTo("이의사");
		assertThat(foundVet.getSpecialties()).hasSize(2);
	}

	@Test
	@DisplayName("전문 분야별 수의사 조회 성공")
	void findVetsBySpecialtyIdSuccess() {
		List<VetResponseDto> vets = vetService.findBySpecialtyId(2);

		assertThat(vets).isNotEmpty();
		assertThat(vets).hasSize(1);
		assertThat(vets.get(0).getName()).isEqualTo("강의사");
		assertThat(vets.get(0).getSpecialties().stream()
			.anyMatch(s -> s.getName().equals("소아과"))).isTrue();
	}
}
