package domain.pet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.common.error.PetErrorCode;
import org.springframework.samples.petclinic.common.exception.ApiException;
import org.springframework.samples.petclinic.domain.pet.dto.PetRequestDto;
import org.springframework.samples.petclinic.domain.pet.dto.PetResponseDto;
import org.springframework.samples.petclinic.domain.pet.mapper.PetMapper;
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

	@Mock
	private PetMapper petMapper;

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
			.type(petType)
			.owner(owner)
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
		when(petMapper.toDto(any(Pet.class))).thenReturn(petResponseDto);

		List<PetResponseDto> result = petService.getAllPets();

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(pet.getId());

		verify(petRepository, times(1)).findAll();
		verify(petMapper, times(1)).toDto(any(Pet.class));
	}

	@Test
	@DisplayName("단일 Pet 조회 성공")
	void getPetById_Success() {
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));
		when(petMapper.toDto(any(Pet.class))).thenReturn(petResponseDto);

		PetResponseDto result = petService.getPetById(1);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(pet.getId());

		verify(petRepository, times(1)).findById(1);
		verify(petMapper, times(1)).toDto(any(Pet.class));
	}

	@Test
	@DisplayName("단일 Pet 조회 실패 - Pet Not Found")
	void getPetById_Failure() {
		// Given
		when(petRepository.findById(999)).thenReturn(Optional.empty());

		// When, Then
		assertThatThrownBy(() -> petService.getPetById(999))
			.isInstanceOf(ApiException.class)
			.hasMessage(PetErrorCode.NO_PET.getDescription());

		verify(petRepository, times(1)).findById(999);
		verifyNoInteractions(petMapper);
	}

	@Test
	@DisplayName("주인의 펫 조회 성공")
	void getPetsByOwnerId_Success() {
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
		when(petRepository.findAll()).thenReturn(List.of(pet));
		when(petMapper.toDto(any(Pet.class))).thenReturn(petResponseDto);

		List<PetResponseDto> result = petService.getPetsByOwnerId(1);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getOwnerId()).isEqualTo(1);

		verify(ownerRepository, times(1)).findById(1);
		verify(petRepository, times(1)).findAll();
		verify(petMapper, times(1)).toDto(any(Pet.class));
	}

	@Test
	@DisplayName("주인의 펫 조회 실패 - Invalid Owner")
	void getPetsByOwnerId_Failure_InvalidOwner() {
		// Given
		when(ownerRepository.findById(99)).thenReturn(Optional.empty());

		// When, Then
		assertThatThrownBy(() -> petService.getPetsByOwnerId(99))
			.isInstanceOf(ApiException.class)
			.hasMessage(PetErrorCode.INVALID_OWNER.getDescription());

		verify(ownerRepository, times(1)).findById(99);
		verifyNoInteractions(petRepository, petMapper);
	}

	@Test
	@DisplayName("Pet 생성 성공")
	void createPet_Success() {
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
		when(petMapper.toEntity(any(PetRequestDto.class), any(PetType.class), any(Owner.class))).thenReturn(pet);
		when(petRepository.save(any(Pet.class))).thenReturn(pet);
		when(petMapper.toDto(any(Pet.class))).thenReturn(petResponseDto);

		PetResponseDto result = petService.createPet(petRequestDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(pet.getId());

		verify(petTypeRepository, times(1)).findById(1);
		verify(ownerRepository, times(1)).findById(1);
		verify(petMapper, times(1)).toEntity(any(PetRequestDto.class), any(PetType.class), any(Owner.class));
		verify(petRepository, times(1)).save(any(Pet.class));
		verify(petMapper, times(1)).toDto(any(Pet.class));
	}

	@Test
	@DisplayName("Pet 생성 실패 - Invalid PetType")
	void createPet_Failure_InvalidPetType() {
		when(petTypeRepository.findById(99)).thenReturn(Optional.empty());
		petRequestDto.setTypeId(99);

		assertThatThrownBy(() -> petService.createPet(petRequestDto))
			.isInstanceOf(ApiException.class)
			.hasMessage(PetErrorCode.INVALID_PET_TYPE.getDescription());

		verify(petTypeRepository, times(1)).findById(99);
		verifyNoInteractions(ownerRepository, petRepository, petMapper);
	}

	@Test
	@DisplayName("Pet 생성 실패 - Invalid Owner")
	void createPet_Failure_InvalidOwner() {
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(ownerRepository.findById(99)).thenReturn(Optional.empty());
		petRequestDto.setOwnerId(99);

		assertThatThrownBy(() -> petService.createPet(petRequestDto))
			.isInstanceOf(ApiException.class)
			.hasMessage(PetErrorCode.INVALID_OWNER.getDescription());

		verify(petTypeRepository, times(1)).findById(1);
		verify(ownerRepository, times(1)).findById(99);
		verifyNoInteractions(petRepository, petMapper);
	}

	@Test
	@DisplayName("Pet 수정 성공")
	void updatePet_Success() {
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
		when(petRepository.save(any(Pet.class))).thenReturn(pet);
		when(petMapper.toDto(any(Pet.class))).thenReturn(petResponseDto);

		PetResponseDto result = petService.updatePet(1, petRequestDto);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(pet.getId());

		verify(petRepository, times(1)).findById(1);
		verify(petTypeRepository, times(1)).findById(1);
		verify(ownerRepository, times(1)).findById(1);
		verify(petRepository, times(1)).save(any(Pet.class));
		verify(petMapper, times(1)).toDto(any(Pet.class));
	}

	@Test
	@DisplayName("Pet 수정 실패 - Pet Not Found")
	void updatePet_Failure_NotFound() {
		when(petRepository.findById(99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> petService.updatePet(99, petRequestDto))
			.isInstanceOf(ApiException.class)
			.hasMessage(PetErrorCode.NO_PET.getDescription());

		verify(petRepository, times(1)).findById(99);
		verifyNoInteractions(petTypeRepository, ownerRepository, petMapper);
	}

	@Test
	@DisplayName("Pet 수정 실패 - Invalid PetType")
	void updatePet_Failure_InvalidPetType() {
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));
		when(petTypeRepository.findById(99)).thenReturn(Optional.empty());
		petRequestDto.setTypeId(99);

		assertThatThrownBy(() -> petService.updatePet(1, petRequestDto))
			.isInstanceOf(ApiException.class)
			.hasMessage(PetErrorCode.INVALID_PET_TYPE.getDescription());

		verify(petRepository, times(1)).findById(1);
		verify(petTypeRepository, times(1)).findById(99);
		verifyNoInteractions(ownerRepository, petMapper);
	}

	@Test
	@DisplayName("Pet 수정 실패 - Invalid Owner")
	void updatePet_Failure_InvalidOwner() {
		when(petRepository.findById(1)).thenReturn(Optional.of(pet));
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(ownerRepository.findById(99)).thenReturn(Optional.empty());
		petRequestDto.setOwnerId(99);

		assertThatThrownBy(() -> petService.updatePet(1, petRequestDto))
			.isInstanceOf(ApiException.class)
			.hasMessage(PetErrorCode.INVALID_OWNER.getDescription());

		verify(petRepository, times(1)).findById(1);
		verify(petTypeRepository, times(1)).findById(1);
		verify(ownerRepository, times(1)).findById(99);
		verifyNoInteractions(petMapper);
	}
}
