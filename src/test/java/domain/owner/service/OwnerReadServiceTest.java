package domain.owner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.domain.owner.dto.OwnerResponseDto;
import org.springframework.samples.petclinic.domain.owner.exception.OwnerNotFoundException;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.owner.repository.OwnerRepository;
import org.springframework.samples.petclinic.domain.owner.service.OwnerReadService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OwnerReadService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class OwnerReadServiceTest {

	@Mock
	private OwnerRepository ownerRepository;

	@InjectMocks
	private OwnerReadService ownerReadService;

	private Owner owner1;
	private Owner owner2;

	@BeforeEach
	void setUp() {
		owner1 = Owner.builder()
			.id(1)
			.name("구름")
			.address("강남")
			.telephone("1234567890")
			.city("서울")
			.build();

		owner2 = Owner.builder()
			.id(2)
			.name("구르미")
			.address("해운대")
			.telephone("0987654321")
			.city("부산")
			.build();
	}

	@Test
	@DisplayName("모든 회원 조회 성공")
	void findAll_Success() {
		// given
		when(ownerRepository.findAll()).thenReturn(Arrays.asList(owner1, owner2));

		// when
		List<OwnerResponseDto> result = ownerReadService.findAll();

		// then
		assertNotNull(result);
		assertEquals(2, result.size());

		assertEquals("구름", result.get(0).getName());
		assertEquals("구르미", result.get(1).getName());
		verify(ownerRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("회원 ID로 조회 성공")
	void findById_Success() {
		// given
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner1));

		// when
		OwnerResponseDto result = ownerReadService.findById(1);

		// then
		assertNotNull(result);
		assertEquals("구름", result.getName());
		assertEquals("강남", result.getAddress());
		assertEquals("1234567890", result.getTelephone());
		assertEquals("서울", result.getCity());
		verify(ownerRepository, times(1)).findById(1);
	}

	@Test
	@DisplayName("회원 ID로 조회 실패 - Owner Id가 없을 때")
	void findById_Fail_OwnerNotFound() {
		// given
		when(ownerRepository.findById(3)).thenReturn(Optional.empty());

		// when & then
		OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class, () ->
			ownerReadService.findById(3)
		);
		assertEquals("Owner not found with id 3", exception.getMessage());
		verify(ownerRepository, times(1)).findById(3);
	}
}
