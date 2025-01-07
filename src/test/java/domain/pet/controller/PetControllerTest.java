package domain.pet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.domain.pet.controller.PetController;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.exception.InvalidOwnerException;
import org.springframework.samples.petclinic.domain.pet.exception.InvalidPetTypeException;
import org.springframework.samples.petclinic.domain.pet.exception.PetNotFoundException;
import org.springframework.samples.petclinic.domain.pet.service.PetService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetControllerTest {

	@InjectMocks
	private PetController petController;

	@Mock
	private PetService petService;

	private PetRequestDto petRequestDto;
	private PetResponseDto petResponseDto;

	@BeforeEach
	void setUp() {
		petRequestDto = new PetRequestDto();
		petRequestDto.setName("강아지");
		petRequestDto.setBirthDate(LocalDate.of(2000, 1, 1));
		petRequestDto.setTypeId(1);
		petRequestDto.setOwnerId(1);

		petResponseDto = PetResponseDto.builder()
			.id(1)
			.name("강아지")
			.birthDate(LocalDate.of(2000, 1, 1))
			.typeId(1)
			.ownerId(1)
			.build();
	}

	@Test
	@DisplayName("모든 Pet 조회 성공")
	void getAllPets_Success() {
		when(petService.getAllPets()).thenReturn(List.of(petResponseDto));

		ResponseEntity<List<PetResponseDto>> response = petController.getAllPets();

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().size()).isEqualTo(1);
		assertThat(response.getBody().get(0).getId()).isEqualTo(petResponseDto.getId());

		verify(petService, times(1)).getAllPets();
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("단일 Pet 조회 성공")
	void getPetById_Success() {
		when(petService.getPetById(1)).thenReturn(petResponseDto);

		ResponseEntity<PetResponseDto> response = petController.getPetById(1);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(petResponseDto.getId());

		verify(petService, times(1)).getPetById(1);
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("단일 Pet 조회 실패 - Pet not found")
	void getPetById_Failure() {
		when(petService.getPetById(99)).thenThrow(new PetNotFoundException(PetErrorCode.NO_PET));

		Throwable exception = assertThrows(PetNotFoundException.class, () -> petController.getPetById(99));

		assertThat(exception).isInstanceOf(PetNotFoundException.class);
		assertThat(exception.getMessage()).isEqualTo(PetErrorCode.NO_PET.getDescription());

		verify(petService, times(1)).getPetById(99);
	}

	@Test
	@DisplayName("주인의 펫 조회 성공")
	void getPetsByOwnerId_Success() {
		when(petService.getPetsByOwnerId(1)).thenReturn(List.of(petResponseDto));

		ResponseEntity<List<PetResponseDto>> response = petController.getPetsByOwnerId(1);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().size()).isEqualTo(1);
		assertThat(response.getBody().get(0).getOwnerId()).isEqualTo(1);

		verify(petService, times(1)).getPetsByOwnerId(1);
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("주인의 펫 조회 실패 - No pets found")
	void getPetsByOwnerId_Failure_NoPets() {
		when(petService.getPetsByOwnerId(99)).thenReturn(List.of());

		ResponseEntity<List<PetResponseDto>> response = petController.getPetsByOwnerId(99);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().isEmpty()).isTrue();

		verify(petService, times(1)).getPetsByOwnerId(99);
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("주인의 펫 조회 실패 - Invalid Owner")
	void getPetsByOwnerId_Failure_InvalidOwner() {
		when(petService.getPetsByOwnerId(99)).thenThrow(new InvalidOwnerException(PetErrorCode.INVALID_OWNER));

		Throwable exception = assertThrows(InvalidOwnerException.class, () -> petController.getPetsByOwnerId(99));

		assertThat(exception).isInstanceOf(InvalidOwnerException.class);
		assertThat(exception.getMessage()).isEqualTo(PetErrorCode.INVALID_OWNER.getDescription());

		verify(petService, times(1)).getPetsByOwnerId(99);
	}

	@Test
	@DisplayName("Pet 생성 성공")
	void createPet_Success() {
		when(petService.createPet(petRequestDto)).thenReturn(petResponseDto);

		ResponseEntity<PetResponseDto> response = petController.createPet(petRequestDto);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(petResponseDto.getId());

		verify(petService, times(1)).createPet(petRequestDto);
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("Pet 생성 실패 - Invalid PetType")
	void createPet_Failure_InvalidPetType() {
		when(petService.createPet(petRequestDto)).thenThrow(new InvalidPetTypeException(PetErrorCode.INVALID_PET_TYPE));

		Throwable exception = assertThrows(InvalidPetTypeException.class, () -> petController.createPet(petRequestDto));

		assertThat(exception).isInstanceOf(InvalidPetTypeException.class);
		assertThat(exception.getMessage()).isEqualTo(PetErrorCode.INVALID_PET_TYPE.getDescription());

		verify(petService, times(1)).createPet(petRequestDto);
	}

	@Test
	@DisplayName("Pet 수정 성공")
	void updatePet_Success() {
		when(petService.updatePet(1, petRequestDto)).thenReturn(petResponseDto);

		ResponseEntity<PetResponseDto> response = petController.updatePet(1, petRequestDto);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(petResponseDto.getId());

		verify(petService, times(1)).updatePet(1, petRequestDto);
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("Pet 수정 실패 - Pet not found")
	void updatePet_Failure_NotFound() {
		when(petService.updatePet(99, petRequestDto)).thenThrow(new IllegalArgumentException("Pet not found"));

		Throwable exception = assertThrows(IllegalArgumentException.class, () -> petController.updatePet(99, petRequestDto));

		assertThat(exception).isInstanceOf(IllegalArgumentException.class);
		assertThat(exception.getMessage()).isEqualTo("Pet not found");

		verify(petService, times(1)).updatePet(99, petRequestDto);
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("Pet 삭제 성공")
	void deletePet_Success() {
		doNothing().when(petService).deletePet(1);

		ResponseEntity<Void> response = petController.deletePet(1);

		assertThat(response.getStatusCodeValue()).isEqualTo(204);

		verify(petService, times(1)).deletePet(1);
		verifyNoMoreInteractions(petService);
	}

	@Test
	@DisplayName("Pet 삭제 실패 - Pet Not Found")
	void deletePet_Failure_NotFound() {
		doThrow(new PetNotFoundException(PetErrorCode.NO_PET))
			.when(petService).deletePet(99);

		Throwable exception = assertThrows(PetNotFoundException.class, () -> petController.deletePet(99));

		assertThat(exception).isInstanceOf(PetNotFoundException.class);
		assertThat(exception.getMessage()).isEqualTo(PetErrorCode.NO_PET.getDescription());

		verify(petService, times(1)).deletePet(99);
	}
}
