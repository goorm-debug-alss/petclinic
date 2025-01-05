package org.springframework.samples.petclinic.domain.vet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.vet.model.VetSpeciality;

import java.util.List;

public interface VetSpecialtyRepository extends JpaRepository<VetSpeciality, Integer> {
	List<VetSpeciality> findVetSpecialtiesByVetId_Id(int vetId);

	void deleteAllByVetId_Id(int vetId);

	// 전문분야별 수의사 찾기
	List<VetSpeciality> findVetIdsBySpecialtyId_Id(int specialtyId);
}
