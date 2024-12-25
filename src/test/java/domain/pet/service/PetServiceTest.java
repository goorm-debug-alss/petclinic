package domain.pet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.pet.model.PetType;
import org.springframework.samples.petclinic.domain.pet.repository.PetRepository;
import org.springframework.samples.petclinic.domain.pet.repository.PetTypeRepository;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.pet.service.PetService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

	@InjectMocks
	private PetService petService;

	@Mock
	private PetRepository petRepository;

	@Mock
	private PetTypeRepository petTypeRepository;

	@Mock
	private OwnerRepository ownerRepository;

	private Pet pet;
	private PetType petType;
	private Owner owner;
	private PetRequestDto petRequestDto;
	private PetResponseDto petResponseDto;

	@BeforeEach
	void setUp() {
		petType = PetType.builder().id(1).name("Dog").build();
		owner = Owner.builder().id(1).build();
		pet = Pet.builder()
			.id(1)
			.name("강아지")
			.birthDate(LocalDate.of(2020, 1, 1))
			.typeId(petType)
			.ownerId(owner)
			.build();

		petRequestDto = new PetRequestDto();
		petRequestDto.setName("강아지");
		petRequestDto.setBirthDate(LocalDate.of(2020, 1, 1));
		petRequestDto.setTypeId(1);
		petRequestDto.setOwnerId(1);

		petResponseDto = PetResponseDto.builder()
			.id(1)
			.name("강아지")
			.birthDate(LocalDate.of(2020, 1, 1))
			.typeId(1)
			.ownerId(1)
			.build();
	}

	@Test
	@DisplayName("모든 Pet 조회 성공")
	void getAllPets_Success() {
		when(petRepository.findAll()).thenReturn(List.of(pet));

		List<PetResponseDto> result = petService.getAllPets();

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(pet.getId());

		verify(petRepository, times(1)).findAll();
		verifyNoMoreInteractions(petRepository);
	}

	@Test
	@DisplayName("단일 Pet 조회 성공")
	void getPetById_Success() {
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));

		PetResponseDto result = petService.getPetById(1);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(pet.getId());

		verify(petRepository, times(1)).findById(1);
		verifyNoMoreInteractions(petRepository);
	}

	@Test
	@DisplayName("단일 Pet 조회 실패 - Pet not found")
	void getPetById_Failure() {
		when(petRepository.findById(1)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> petService.getPetById(1))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Pet not found");

		verify(petRepository, times(1)).findById(1);
		verifyNoMoreInteractions(petRepository);
	}

	@Test
	@DisplayName("주인의 펫 조회 성공")
	void getPetsByOwnerId_Success() {
		when(petRepository.findAll()).thenReturn(List.of(pet));

		List<PetResponseDto> result = petService.getPetsByOwnerId(1);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getOwnerId()).isEqualTo(1);

		verify(petRepository, times(1)).findAll();
		verifyNoMoreInteractions(petRepository);
	}

	@Test
	@DisplayName("주인의 펫 조회 실패 - No pets found")
	void getPetsByOwnerId_Failure_NoPets() {
		when(petRepository.findAll()).thenReturn(List.of());

		List<PetResponseDto> result = petService.getPetsByOwnerId(1);

		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(petRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Pet 생성 성공")
	void createPet_Success() {
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
		when(petRepository.save(any(Pet.class))).thenReturn(pet);

		PetResponseDto result = petService.createPet(petRequestDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(pet.getId());

		verify(petTypeRepository, times(1)).findById(1);
		verify(ownerRepository, times(1)).findById(1);
		verify(petRepository, times(1)).save(any(Pet.class));
		verifyNoMoreInteractions(petTypeRepository, ownerRepository, petRepository);
	}

	@Test
	@DisplayName("Pet 생성 실패 - Invalid PetType ID")
	void createPet_Failure_InvalidPetTypeId() {
		petRequestDto.setTypeId(99);
		when(petTypeRepository.findById(99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> petService.createPet(petRequestDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Invalid PetType ID");

		verify(petTypeRepository, times(1)).findById(99);
		verifyNoInteractions(petRepository, ownerRepository);
	}

	@Test
	@DisplayName("Pet 수정 성공")
	void updatePet_Success() {
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
		when(petRepository.save(any(Pet.class))).thenReturn(pet);

		PetResponseDto result = petService.updatePet(1, petRequestDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(pet.getId());

		verify(petRepository, times(1)).findById(1);
		verify(petTypeRepository, times(1)).findById(1);
		verify(ownerRepository, times(1)).findById(1);
		verify(petRepository, times(1)).save(any(Pet.class));
		verifyNoMoreInteractions(petRepository, petTypeRepository, ownerRepository);
	}

	@Test
	@DisplayName("Pet 수정 실패 - Pet not found")
	void updatePet_Failure_NotFound() {
		when(petRepository.findById(99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> petService.updatePet(99, petRequestDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Pet not found");

		verify(petRepository, times(1)).findById(99);
		verifyNoInteractions(petTypeRepository, ownerRepository);
	}

	@Test
	@DisplayName("Pet 삭제 성공")
	void deletePet_Success() {
		doNothing().when(petRepository).deleteById(1);

		petService.deletePet(1);

		verify(petRepository, times(1)).deleteById(1);
		verifyNoMoreInteractions(petRepository);
	}

	@Test
	@DisplayName("Pet 삭제 실패 - Pet not found")
	void deletePet_Failure_NotFound() {
		doThrow(new IllegalArgumentException("Pet not found")).when(petRepository).deleteById(99);

		assertThatThrownBy(() -> petService.deletePet(99))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Pet not found");

		verify(petRepository, times(1)).deleteById(99);
	}
}
